package RM1;

public class TORServerRM1 {

    public ServerImplementation serverOBJ;
    private String serverName = "TORServerRM1";

    public TORServerRM1() throws Exception {

        int localUDPPortNumber = ServerInterface.UDP_PORT_TOR;
        int firstRemoteUDPPortNumber = ServerInterface.UDP_PORT_OTW;
        int secondRemonteUDPPortNumber = ServerInterface.UDP_PORT_MTL;

        serverOBJ = new ServerImplementation(firstRemoteUDPPortNumber,secondRemonteUDPPortNumber,serverName);

        System.out.println("TORServerRM1 online");

        ServerListenThread serverListenThread = new ServerListenThread(serverOBJ, localUDPPortNumber);
        serverListenThread.start();

    }
}
