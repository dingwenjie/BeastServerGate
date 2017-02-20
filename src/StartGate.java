import com.chen.server.GateServer;



public class StartGate {

	public static void main(String[] args)
	{
		new Thread((Runnable)GateServer.getInstance()).start();
	}
}
