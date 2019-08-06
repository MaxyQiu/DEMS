package RM2;

public class MTLServerRM2 {
	
	public  ServerImplementation serverOBJ;
    private  String serverName = "MTLServerRM2";

    public MTLServerRM2() throws Exception {

        int localUDPPortNumber = ServerInterface.UDP_PORT_MTL;
        int firstRemoteUDPPortNumber = ServerInterface.UDP_PORT_OTW;
        int secondRemonteUDPPortNumber = ServerInterface.UDP_PORT_TOR;

        serverOBJ = new ServerImplementation(firstRemoteUDPPortNumber,secondRemonteUDPPortNumber,serverName);

        System.out.println("MTLServerRM2 online");

        ServerListenThread serverListenThread = new ServerListenThread(serverOBJ, localUDPPortNumber);
        serverListenThread.start();

    }
}
