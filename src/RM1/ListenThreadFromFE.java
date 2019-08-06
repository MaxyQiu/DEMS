package RM1;

import IPPortAddress.IPPortAddress;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ListenThreadFromFE extends Thread{

    private ReplicaManager1 rm1;

    public ListenThreadFromFE(ReplicaManager1 rm1){
        this.rm1 = rm1;
    }

    @Override
    public void run(){
        DatagramSocket listenSocketFromFE = null;

        try {
            listenSocketFromFE = new DatagramSocket(IPPortAddress.RM1_UDP_PORT);
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket FEMessage = new DatagramPacket(buffer, buffer.length);
                listenSocketFromFE.receive(FEMessage);
                String messages = new String(FEMessage.getData()).trim();
                String[] messagesSplit = messages.split("~");
                if (messagesSplit[0].equals("R")) {
                    if (messagesSplit[1].equals("f")) {
                        rm1.restartF();
                    }
                    if (messagesSplit[1].equals("d")) {
                        rm1.restartD();
                    }
                }
            }
            } catch(IOException e){
                e.printStackTrace();
            }

    }
}
