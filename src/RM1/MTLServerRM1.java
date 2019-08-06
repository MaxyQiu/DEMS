package RM1;

public class MTLServerRM1 {

    public  ServerImplementation serverOBJ;
    private  String serverName = "MTLServerRM1";

    public MTLServerRM1() throws Exception {

        int localUDPPortNumber = ServerInterface.UDP_PORT_MTL;
        int firstRemoteUDPPortNumber = ServerInterface.UDP_PORT_OTW;
        int secondRemonteUDPPortNumber = ServerInterface.UDP_PORT_TOR;

        serverOBJ = new ServerImplementation(firstRemoteUDPPortNumber,secondRemonteUDPPortNumber,serverName);

        System.out.println("MTLServerRM1 online");

        ServerListenThread serverListenThread = new ServerListenThread(serverOBJ, localUDPPortNumber);
        serverListenThread.start();

    }
}
