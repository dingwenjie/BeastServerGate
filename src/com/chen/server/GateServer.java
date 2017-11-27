package com.chen.server;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.chen.cache.executor.NonOrderedQueuePoolExecutor;
import com.chen.cache.executor.OrderedQueuePoolExecutor;
import com.chen.cache.structs.AbstractWork;
import com.chen.command.Handler;
import com.chen.message.Message;
import com.chen.message.MessagePool;
import com.chen.message.TransfersMessage;
import com.chen.mina.impl.ClientServer;
import com.chen.mina.impl.InnerServer;
import com.chen.mina.impl.MinaServer;
import com.chen.player.manager.PlayerManager;
import com.chen.player.structs.Player;
import com.chen.server.config.GameConfig;
import com.chen.server.loader.GameConfigXmlLoader;
import com.chen.server.message.req.ReqGateRegisterWorldMessage;
import com.chen.util.MessageUtil;
import com.chen.util.SessionUtil;

public class GateServer extends MinaServer
{
	private static Logger log = LogManager.getLogger(GateServer.class);
	
	private static Logger innerCloselog = LogManager.getLogger("InnerSessionClose");
	private static Logger messagelog = LogManager.getLogger("GateMessage");
	// 默认Mina服务器配置文件
	private static final String defaultMinaServerConfig = "BeastServerGate/gate-config/mina-server-config.xml";
	// 默认内部客户服务器配置文件
	private static final String defaultClientServerConfig = "BeastServerGate/gate-config/client-server-config.xml";
	private static final String defaultInnerServerConfig = "BeastServerGate/gate-config/Inner-server-config.xml";
	private static final String defaultGameConfig = "BeastServerGate/gate-config/game-config.xml";
	private static GameConfig config;
	private static Object obj = new Object();
	//玩家通信列表
	private static ConcurrentHashMap<String, IoSession> user_session = new ConcurrentHashMap<String, IoSession>();
	//角色通信列表
	private static ConcurrentHashMap<Long, IoSession> player_session = new ConcurrentHashMap<Long, IoSession>();
	private static ConcurrentHashMap<Integer,List<IoSession>> gameSessions = new ConcurrentHashMap<Integer, List<IoSession>>();
	private static MessagePool messagePool = new MessagePool();
	private NonOrderedQueuePoolExecutor actionExecutor = new NonOrderedQueuePoolExecutor(500);
	private OrderedQueuePoolExecutor recvExecutor = new OrderedQueuePoolExecutor("消息接收队列",100,10000);
	private OrderedQueuePoolExecutor sendExecutor = new OrderedQueuePoolExecutor("消息发送队列", 100,-1);
	private static GateServer server;
	private static ClientServer clientServer = null;
	private static InnerServer innerServer = null;
	private boolean connectSuccess = false;
	public GateServer()
	{
		this(defaultMinaServerConfig,defaultInnerServerConfig,defaultClientServerConfig,defaultGameConfig);
	}
	public GateServer(String minaServerConfig,String innerServerConfig,String clientServerConfig,String gameConfig)
	{
		super(minaServerConfig);
		clientServer = new ClientConnectServer(clientServerConfig);
		innerServer = new InnerConnectServer(innerServerConfig);
		setConfig(new GameConfigXmlLoader().load(gameConfig));
	}
	public static GateServer getInstance()
	{
		synchronized (obj)
		{
			if (server == null)
			{
				server = new GateServer();
			}
		}
		return server;
	}
	public static GateServer getInstance(String minaServerConfig,String innerServerConfig,String clientServerConfig,String gameConfig)
	{
		synchronized (obj)
		{
			if (server == null)
			{
				server = new GateServer(minaServerConfig,innerServerConfig,clientServerConfig, gameConfig);
			}
		}
		return server;
	}
	@Override
	public void run()
	{
		//long begin = System.currentTimeMillis();
		super.run();
		new Thread(clientServer).start();
		new Thread(innerServer).start();
		try {
			//外网消息定时发送
			new Timer("Send-Timer").schedule(new TimerTask() {
				@Override
				public void run() {
					List<IoSession> sessions = new ArrayList<IoSession>();
					synchronized (player_session) {
						sessions.addAll(player_session.values());
					}

					for (IoSession ioSession : sessions) {
						IoBuffer sendbuf = null;
						synchronized (ioSession) {
							if (ioSession.containsAttribute("send_buf")) {
								sendbuf = (IoBuffer) ioSession
										.getAttribute("send_buf");
								ioSession.removeAttribute("send_buf");
							}
						}
						try {
							if (sendbuf != null && sendbuf.position() > 0) {
								sendbuf.flip();
								ioSession.write(sendbuf);
							}
						} catch (Exception e) {
							continue;
						}
					}
				}
			}, 1, 1);

			//内网消息定时发送
			new Timer("Inner-Send-Timer").schedule(new TimerTask(){
				@Override
				public void run() {
					List<IoSession> sessions = new ArrayList<IoSession>();
					synchronized (gameSessions) {
						Iterator<List<IoSession>> iter = gameSessions.values().iterator();
						while (iter.hasNext()) {
							List<IoSession> list = (List<IoSession>) iter.next();
							sessions.addAll(list);
						}
					}
					for (IoSession ioSession : sessions) {
						IoBuffer sendbuf = null;
						synchronized (ioSession) {
							if(ioSession.containsAttribute("send_buf")){
								sendbuf = (IoBuffer)ioSession.getAttribute("send_buf");
								ioSession.removeAttribute("send_buf");
							}
						}
						try{
							if (sendbuf != null && sendbuf.position() > 0) {
								sendbuf.flip();
								WriteFuture wf = ioSession.write(sendbuf);
								wf.await();
							}
						}catch (Exception e) {
							continue;
						}
					}
				}
			}, 1, 1);
		} catch (Exception e) {
			log.error(e,e);
		}
		// 关闭空的session连接
		new Timer("Close-Session-Timer").schedule(new TimerTask() {

			@Override
			public void run() {
				if (acceptor == null || acceptor.getManagedSessions() == null
						|| acceptor.getManagedSessions().size() == 0)
					return;
				long now = System.currentTimeMillis();

				IoSession[] sessionArray = acceptor.getManagedSessions()
						.values().toArray(new IoSession[0]);
				for (IoSession ioSession : sessionArray) {
					if (ioSession != null && ioSession.isConnected()) {
						if (now - ioSession.getCreationTime() > 10 * 1000
								&& !ioSession
										.containsAttribute("user_id")) {
							SessionUtil.closeSession(ioSession, "10秒内没有发送登陆信息");
						} else if (acceptor.getManagedSessionCount() > 5000
								&& ioSession.containsAttribute("pre_heart")) {
							long pre = (Long) ioSession.getAttribute("pre_heart");
							if (now - pre > 5 * 60 * 1000) {
								SessionUtil.closeSession(ioSession,
										"5分钟内没有发送心跳信息");
							}
						}
					}
				}
			}
		}, 5 * 1000, 5 * 1000);
		while (!connectSuccess)
		{
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				log.error(e,e);
			}
		}
	}
	@Override
	public void sessionCreate(IoSession session) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sessionIdle(IoSession session, IdleStatus arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sessionOpened(IoSession session) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void doCommand(IoSession session, IoBuffer buf) 
	{
		try {
			//连接中心服务器失败
			if (!connectSuccess)
			{
				SessionUtil.closeSession(session, "连接中心服务器失败！");
				return;
			}
			int id = buf.getInt();//消息id
			System.out.println("收到消息id："+id);
			long sessionId = session.getId();//客户端的通信id
			if (id == 1002)
			{
				log.debug("客户端："+session+"收到登陆消息，时间为："+System.currentTimeMillis());
			}
			if (id != 1002 && !session.containsAttribute("user_id"))
			{
				SessionUtil.closeSession(session, "没有发送登陆消息");
				return;
			}
			//设置前一条消息的id
			session.setAttribute("pre_message", id);
			recvExecutor.addTask(sessionId, new RWork(id, session, buf));
		} catch (Exception e) {
			log.error(e,e);
		}	
	}
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		// TODO Auto-generated method stub
		
	}
	private int lastCloseTime;
	private int closeNum;
	@Override
	public void sessionClosed(IoSession session) {
		int time = (int)(System.currentTimeMillis());
		if (lastCloseTime != time)
		{
			log.error("关闭连接数："+closeNum+",时间:"+lastCloseTime);
			closeNum = 0;
		}
		closeNum++;
		lastCloseTime = time;
		//直接让玩家退出游戏服务器缓存
		StringBuffer stringBuffer = new StringBuffer();
		if (session.containsAttribute("session_ip"))
		{
			stringBuffer.append("IP:"+session.getAttribute("session_ip"));
		}
		if (session.containsAttribute("player_id"))
		{
			stringBuffer.append("Player:"+session.getAttribute("player_id"));
		}
		if (session.containsAttribute("user_id"))
		{
			stringBuffer.append("UserId:"+session.getAttribute("user_id"));
		}
		messagelog.debug(session+"nowtime:"+System.currentTimeMillis()+"close");
		boolean quit = false;
		synchronized(session)
		{
			if (session.containsAttribute("player_id"))
			{
				long roleId = (long)session.getAttribute("player_id");
				Player player = PlayerManager.getInstance().getPlayer(roleId);
				if (player == null)
				{
					log.error("玩家:"+roleId+"没有注册到游戏服务器");
				}
				else
				{
					PlayerManager.getInstance().quit(player, true);
					quit = true;
					
					IoSession ioSession = player_session.get(roleId);
					if (ioSession != null && ioSession.getId() == session.getId())
					{
						removePlayerSession(roleId);
					}
				}
			}
			if (session.containsAttribute("user_id"))
			{
				String userId = (String)session.getAttribute("user_id");
				IoSession session1 = user_session.get(userId);
				if (session1 != null && session.getId() == session1.getId())
				{
					user_session.remove(userId);
					if (!quit)
					{
						PlayerManager.getInstance().quit(session);
					}
				}
			}
		}
	}
	@Override
	protected void stop() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 根据玩家id获得该玩家客户端的通信
	 * @param server
	 * @param userId
	 * @return 客户端的通信
	 */
	public IoSession getSessionByUser(int server, String userId)
	{
		System.out.println(userId);
		System.out.println(user_session.keys().toString());
		if (user_session.containsKey(userId))
		{
			System.out.println("Contain User");
			return user_session.get(userId);
		}
		return null;
	}
	/**
	 * 根据用户角色的id找到该玩家的session
	 * @param roleId
	 * @return
	 */
	public IoSession getSessionByRole(long roleId)
	{
		if (player_session.containsKey(roleId))
		{
			return player_session.get(roleId);
		}
		return null;
	}
	/**
	 * 根据玩家id移除玩家通信
	 * @param playId
	 */
	public void removePlayerSession(long playId)
	{
		synchronized (player_session) {
			player_session.remove(playId);
		}
	}
	/**
	 * 根据用户id移除用户通信
	 * @param userId
	 */
	public void removeUserSession(String userId)
	{
		user_session.remove(userId);
	}
	/**
	 * 玩家登陆后注册玩家，加入到User通信列表中
	 * 设置serverId，user_id，isAudlt属性
	 * @param session
	 * @param server
	 * @param userId
	 * @param isAdult
	 */
	public void registerUser(IoSession session, int server,String userId,int isAdult)
	{
		synchronized (session)
		{
			session.setAttribute("server_id", server);
			session.setAttribute("user_id", userId);
			session.setAttribute("is_adult", isAdult);
			user_session.put(userId, session);
		}
	}
	/**
	 * 注册角色到角色Map中，并设置Session的“role_id”属性
	 * @param session
	 * @param roleId
	 */
	public void registerRole(IoSession session,long roleId)
	{
		synchronized (player_session)
		{
			session.setAttribute("role_id",roleId);
			player_session.put(roleId, session);
		}
	}
	/**
	 * 网关注册游戏服务器
	 * @param id
	 * @param session
	 */
	public synchronized void registerGameServer(int id,IoSession session)
	{
		session.setAttribute("server_id",id);
		synchronized (gameSessions)
		{
			List<IoSession> sessions = gameSessions.get(id);
			if (sessions != null)
			{
				sessions.add(session);
			}
			else
			{
				sessions = new ArrayList<IoSession>();
				gameSessions.put(id, sessions);
				sessions.add(session);
			}
			System.out.println(gameSessions.get(id).get(0).getId());
		}
	}
	/*public synchronized void removeGameServer(int id,IoSession session)
	{
		synchronized (gameSessions)
		{
			List<IoSession> sessions = gameSessions.get(id);
			if (sessions == null)
			{
				sessions = new ArrayList<IoSession>();
				gameSessions.put(id, sessions);
			}
			sessions.add(session);
		}
	}
	*/
	public static GameConfig getConfig() {
		return config;
	}
	public static void setConfig(GameConfig config) {
		GateServer.config = config;
	}
	/**
	 * 获得与游戏服务器通讯的session
	 * @param server
	 * @return
	 */
	public List<IoSession> getGameSession(int server)
	{
		return gameSessions.get(server);
	}
	/**
	 * 游戏服务器移除
	 * @param id
	 * @param session
	 */
	private void removeGameServer(int id,IoSession session)
	{
		synchronized (gameSessions) 
		{
			List<IoSession> sessions = gameSessions.get(id);
			if (sessions!= null)
			{
				sessions.remove(session);
			}
		}
	}
	private class RWork extends AbstractWork
	{
		private int id;//消息id
		private IoSession session;//客户端的通信
		private IoBuffer buf;//传来的消息byte[],不包括消息id
		public RWork(int id,IoSession session, IoBuffer buf)
		{
			this.id = id;
			this.session = session;
			this.buf = buf;
		}
		@Override
		public void run() {
			try {
				Handler handler = messagePool.getHandler(id);
				if (handler != null)
				{
					//取出消息
					Message msg = messagePool.getMessage(id);
					log.debug("收到消息id："+msg.getId()+"-->"+msg.getClass().getSimpleName());
					msg.read(buf);
					msg.setSession(session);
					handler.setMessage(msg);
					handler.setCreateTime(System.currentTimeMillis());
					actionExecutor.execute(handler);//执行消息，调用Handler的action方法
				}else
				{
					Object roleId = session.getAttribute("role_id");
					if (roleId == null)
					{
						log.error("session:"+session+"没有绑定角色");
						return ;
					}
					long playerId = (Long)roleId;
					TransfersMessage msg = new TransfersMessage();
					msg.setId(id);
					msg.getRoleIds().add(playerId);
					msg.setBytes(new byte[buf.remaining()]);
					buf.get(msg.getBytes());
					Player player = PlayerManager.getInstance().getPlayer(playerId);
					if (player == null)
					{
						log.error("角色"+playerId+"未注册");
						return;
					}
					int sessionId = (Integer)session.getAttribute("session_id");
					MessageUtil.send_to_game(player.getServer(), sessionId, msg);
				}
			} catch (Exception e) {
				log.error(e,e);
			}
			
		}
	}
	private class ClientConnectServer extends ClientServer
	{
		protected ClientConnectServer(String serverConfig)
		{
			super(serverConfig);
		}
		@Override
		public void doCommand(IoSession session, IoBuffer buf) 
		{
			try{
				int id = buf.getInt();//消息id
				System.out.println("内部服务器收到消息id："+id);
				long sendId = buf.getLong();//客户端的sessionId
				int roleNum = buf.getInt();//所拥有的角色数量
				List<Long> roles = new ArrayList<Long>();
				for (int i=0; i<roleNum;i++)
				{
					roles.add(buf.getLong());
				}
				Handler handler = messagePool.getHandler(id);
				if (handler != null)
				{
					Message msg = messagePool.getMessage(id);
					msg.read(buf);
					msg.setSession(session);
					handler.setMessage(msg);
					actionExecutor.execute(handler);
				}
				else
				{
					TransfersMessage msg = new TransfersMessage();
					msg.setId(id);
					msg.setBytes(new byte[buf.remaining()]);
					buf.get(msg.getBytes());
					Work work = new Work(roles, msg);
					sendExecutor.addTask(sendId, work);
				}
			}
			catch(Exception e)
			{
				this.log.error(e,e);
			}
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) {
			// TODO Auto-generated method stub
			innerCloselog.error("中心服务器"+session+"出异常:"+cause,cause);
		}

		@Override
		public void sessionClosed(IoSession session) {
			// TODO Auto-generated method stub
			innerCloselog.error("center"+session + "关闭！");
			removeCenterServer(session);
		}

		@Override
		public void sessionCreate(IoSession arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionIdle(IoSession arg0, IdleStatus arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionOpened(IoSession session) {
			// TODO Auto-generated method stub
			
		}
		@Override
		protected void connectComplete() {		
			connectSuccess = true;
		}
		/**
		 * 网关服务器注册到世界服务器
		 */
		@Override
		public void register(IoSession session, int type) {
			ReqGateRegisterWorldMessage msg = new ReqGateRegisterWorldMessage();
			msg.setServerId(this.getServer_id());
			msg.setServerName(this.getServer_name());
			session.write(msg);
		}

		@Override
		protected void stop() {
			// TODO Auto-generated method stub
			
		}
		private void removeCenterServer(IoSession session)
		{
			synchronized (centerSessions)
			{
				if (centerSessions != null)
				{
					centerSessions.remove(session);
				}
			}
		}
	}
	private class InnerConnectServer extends InnerServer
	{
		protected InnerConnectServer(String serverConfig)
		{
			super(serverConfig);
		}

		@Override
		public void sessionCreate(IoSession session) {
			
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionOpened(IoSession session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void doCommand(IoSession session, IoBuffer buf) 
		{
			try 
			{
				int id = buf.getInt();//消息id
				log.info("网关内部连接服务器收到消息id："+id);
				long sendId = buf.getLong();
				int roleNum = buf.getInt();//角色数量
				List<Long> roles = new ArrayList<Long>();
				for (int i=0;i<roleNum;i++)
				{
					roles.add(buf.getLong());
				}
				Handler handler = messagePool.getHandler(id);
				if (handler != null)
				{
					Message msg = messagePool.getMessage(id);
					msg.read(buf);
					msg.setSession(session);
					handler.setMessage(msg);
					actionExecutor.execute(handler);
				}
				else
				{
					TransfersMessage msg = new TransfersMessage();
					msg.setId(id);
					msg.setBytes(new byte[buf.remaining()]);
					buf.get(msg.getBytes());
					Work work = new Work(roles, msg);
					sendExecutor.addTask(sendId, work);
				}
			} catch (Exception e) {
				log.error(e,e);
			}
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) 
		{
			innerCloselog.error("InnerServer error"+session,cause);			
		}

		@Override
		public void sessionClosed(IoSession session) {
			innerCloselog.error("InnerServer"+session+"关闭");
			if (session.containsAttribute("server_id"))
			{
				int id = (Integer)session.getAttribute("server_id");
				//移除游戏服务器
				removeGameServer(id, session);
			}
		}

		@Override
		protected void stop() {
			// TODO Auto-generated method stub
			
		}
	}	
	private class Work extends AbstractWork
	{
		private List<Long> roles;
		private TransfersMessage msg;
		public Work(List<Long> roles,TransfersMessage msg)
		{
			this.roles = roles;
			this.msg = msg;
		}
		@Override
		public void run() {
			
			try {
				for (int i=0; i<roles.size(); i++)
				{
					MessageUtil.tell_player_message(roles.get(i), msg);
				}
			} catch (Exception e) {
				log.error(e,e);
			}
		}
		
	}
}
