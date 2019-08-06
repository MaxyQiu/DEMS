package RM2;

public class TORServerRM2 {
	
	public ServerImplementation serverOBJ;
    private String serverName = "TORServerRM2";

    public TORServerRM2() throws Exception {

        int localUDPPortNumber = ServerInterface.UDP_PORT_TOR;
        int firstRemoteUDPPortNumber = ServerInterface.UDP_PORT_OTW;
        int secondRemonteUDPPortNumber = ServerInterface.UDP_PORT_MTL;

        serverOBJ = new ServerImplementation(firstRemoteUDPPortNumber,secondRemonteUDPPortNumber,serverName);

        System.out.println("TORServerRM2 online");

        ServerListenThread serverListenThread = new ServerListenThread(serverOBJ, localUDPPortNumber);
        serverListenThread.start();
    }
}
