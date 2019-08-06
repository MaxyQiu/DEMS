package RM2;

public class OTWServerRM2 {
	
	 public ServerImplementation serverOBJ;
	    private String serverName = "OTWServerRM1";

	    public OTWServerRM2() throws Exception {

	        int localUDPPortNumber = ServerInterface.UDP_PORT_OTW;
	        int firstRemoteUDPPortNumber = ServerInterface.UDP_PORT_MTL;
	        int secondRemonteUDPPortNumber = ServerInterface.UDP_PORT_TOR;

	        serverOBJ = new ServerImplementation(firstRemoteUDPPortNumber,secondRemonteUDPPortNumber,serverName);

	        System.out.println("OTWServerRM2 online");

	        ServerListenThread serverListenThread = new ServerListenThread(serverOBJ, localUDPPortNumber);
	        serverListenThread.start();

	    }
}
