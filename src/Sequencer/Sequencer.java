package Sequencer;

import IPPortAddress.IPPortAddress;

import java.io.IOException;
import java.net.*;

public class Sequencer extends Thread{

    private int frontEndUDPPortNumber = IPPortAddress.FRONTEND_UDP_PORT;
    private int sequencerUDPPortNumber = IPPortAddress.SEQUENCER_UDP_PORT;
    private int timeout;//TODO: set time out
    private String multicastIP = IPPortAddress.MULTICAST_IP;
    private int multicastPort = IPPortAddress.MULTICAST_PORT;
    private int jobSequenceNumber = 1;

    public static InetAddress group;
    private MulticastSocket multicastSocket;

    public Sequencer(){
        try {
            group = InetAddress.getByName(multicastIP);
            multicastSocket = new MulticastSocket(multicastPort);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        DatagramSocket listenSocket = null;
        try{
            listenSocket = new DatagramSocket(sequencerUDPPortNumber);
            System.out.println("sequencer UDP Listen Started");
            while (true){
                byte[] buffer = new byte[1000];
                DatagramPacket recieveMessage = new DatagramPacket(buffer, buffer.length);
                listenSocket.receive(recieveMessage);
                System.out.println("message received: " + new String(recieveMessage.getData()));
                String receiveMessageToString = new String(recieveMessage.getData());
                String action = receiveMessageToString.trim();
                if(action.equals("C")){//from rm
                    System.out.println("receive confirmation message from "+ recieveMessage.getAddress().getHostAddress());
                }else{//from fe
                    String requestToRM = jobSequenceNumber+"~"+action;
                    multicastToRM(requestToRM);
                    jobSequenceNumber++;
                }

            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (listenSocket != null)
                listenSocket.close();
        }
    }
    private void multicastToRM(String messageWithNumber){
        byte[] data = messageWithNumber.getBytes();
        try {

            DatagramPacket packet = new DatagramPacket(data,data.length,InetAddress.getByName(multicastIP),multicastPort);

            multicastSocket.send(packet);
            System.out.println("sequencer send job request to each rm");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Sequencer sequencer = new Sequencer();
        sequencer.start();
    }
}
