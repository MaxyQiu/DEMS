package RM4;

import IPPortAddress.IPPortAddress;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListenThreadFromFE extends Thread{
	private ReplicaManager4 rm4;

    public ListenThreadFromFE(ReplicaManager4 rm4){
        this.rm4 = rm4;
    }

    @Override
    public void run(){
        DatagramSocket listenSocketFromFE = null;

        try{
            listenSocketFromFE = new DatagramSocket(IPPortAddress.RM4_UDP_PORT);
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket FEMessage = new DatagramPacket(buffer, buffer.length);
                listenSocketFromFE.receive(FEMessage);
                String messages = new String(FEMessage.getData()).trim();
                String[] messagesSplit = messages.split("~");
                if (messagesSplit[0].equals("R")) {
                    if (messagesSplit[1].equals("f")) {
                        rm4.restartF();
                    }
                    if (messagesSplit[1].equals("d")) {
                        rm4.restartD();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
