package RM2;

import IPPortAddress.IPPortAddress;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ListenThreadFromFE extends Thread{
	private ReplicaManager2 rm2;

    public ListenThreadFromFE(ReplicaManager2 rm2){
        this.rm2 = rm2;
    }

    @Override
    public void run(){
        DatagramSocket listenSocketFromFE = null;

        try{
            listenSocketFromFE = new DatagramSocket(IPPortAddress.RM2_UDP_PORT);
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket FEMessage = new DatagramPacket(buffer, buffer.length);
                listenSocketFromFE.receive(FEMessage);
                String messages = new String(FEMessage.getData()).trim();
                String[] messagesSplit = messages.split("~");
                if (messagesSplit[0].equals("R")) {
                    if (messagesSplit[1].equals("f")) {
                        rm2.restartF();
                    }
                    if (messagesSplit[1].equals("d")) {
                        rm2.restartD();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
