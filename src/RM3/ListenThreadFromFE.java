package RM3;

import IPPortAddress.IPPortAddress;
import RM2.ReplicaManager2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListenThreadFromFE extends Thread{
	private ReplicaManager3 rm3;

    public ListenThreadFromFE(ReplicaManager3 rm3){
        this.rm3 = rm3;
    }

    @Override
    public void run(){
        DatagramSocket listenSocketFromFE = null;

        try{
            listenSocketFromFE = new DatagramSocket(IPPortAddress.RM3_UDP_PORT);
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket FEMessage = new DatagramPacket(buffer, buffer.length);
                listenSocketFromFE.receive(FEMessage);
                String messages = new String(FEMessage.getData()).trim();
                String[] messagesSplit = messages.split("~");
                if (messagesSplit[0].equals("R")) {
                    if (messagesSplit[1].equals("f")) {
                        rm3.restartF();
                    }
                    if (messagesSplit[1].equals("d")) {
                        rm3.restartD();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
