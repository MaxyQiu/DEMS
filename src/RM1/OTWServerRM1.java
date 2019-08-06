package RM1;

public class OTWServerRM1 {

    public ServerImplementation serverOBJ;
    private String serverName = "OTWServerRM1";

    public OTWServerRM1() throws Exception {

        int localUDPPortNumber = ServerInterface.UDP_PORT_OTW;
        int firstRemoteUDPPortNumber = ServerInterface.UDP_PORT_MTL;
        int secondRemonteUDPPortNumber = ServerInterface.UDP_PORT_TOR;

        serverOBJ = new ServerImplementation(firstRemoteUDPPortNumber,secondRemonteUDPPortNumber,serverName);

        System.out.println("OTWServerRM1 online");

        ServerListenThread serverListenThread = new ServerListenThread(serverOBJ, localUDPPortNumber);
        serverListenThread.start();

    }
}
