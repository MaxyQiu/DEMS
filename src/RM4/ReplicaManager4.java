package RM4;

import IPPortAddress.IPPortAddress;
import Sequencer.Sequencer;

import java.io.IOException;
import java.net.*;

public class ReplicaManager4 {

    private static MontrealServer4 mtlServerRM4;
    private static OttawaServer4 otwServerRM4;
    private static TorontoServer4 torServerRM4;

    private String FEIPNumber = IPPortAddress.FRONTEND_IP;
    private int FEUDPPortNumber = IPPortAddress.FRONTEND_UDP_PORT;

    private String multicastIP = IPPortAddress.MULTICAST_IP;
    private int multicastPort = IPPortAddress.MULTICAST_PORT;

    private int ReplicaManagerUDPPortNumber = IPPortAddress.RM4_UDP_SEND_PORT;

    private int biggestSequenceNo = 0;

    private boolean bugF = false;
    private boolean bugD = true;

    public ReplicaManager4() throws Exception {
        //start all three servers
        mtlServerRM4 = new MontrealServer4();
        otwServerRM4 = new OttawaServer4();
        torServerRM4 = new TorontoServer4();
    }

    public static void main(String[] args) throws Exception {
        ReplicaManager4 RM4 = new ReplicaManager4();
        //RM4.start();
        ListenThreadFromFE listenThreadFromFE = new ListenThreadFromFE(RM4);
        listenThreadFromFE.start();

        new Thread(mtlServerRM4).start();
        new Thread(otwServerRM4).start();
        new Thread(torServerRM4).start();

        MulticastSocket listenSocket = null;
        DatagramSocket sendConfirmToSequencer = null;

        try{
            listenSocket = new MulticastSocket(RM4.multicastPort);
            listenSocket.joinGroup(InetAddress.getByName(RM4.multicastIP));

            sendConfirmToSequencer = new DatagramSocket();

            System.out.println("RM4 UDP Listen Started");
            while (true){
                byte[] buffer = new byte[1000];
                DatagramPacket receiveMessage = new DatagramPacket(buffer, buffer.length);
                listenSocket.receive(receiveMessage);
                System.out.println("message received: " + new String(receiveMessage.getData()));

                //message from FE or Sequencer
                String receiveMessageToString = new String(receiveMessage.getData());
                String rawMessages = receiveMessageToString.trim();

                String[] splitMessages = rawMessages.split("~");

                //from fe's multicast warning messages
                if(splitMessages.length==2){
                    String problemReplicaNumber  = splitMessages[0];
                    String problemType = splitMessages[1];
                    //TODO: add log
                }else{//from sequencer
                    //send replay
                    String confirm = "C";
                    byte[] confirmByte = confirm.getBytes();
                    DatagramPacket replayToSequencer = new DatagramPacket(confirmByte,confirm.length(), InetAddress.getByName(IPPortAddress.SEQUENCER_IP),IPPortAddress.SEQUENCER_UDP_PORT );
                    sendConfirmToSequencer.send(replayToSequencer);

                    //TODO: if need to deal with message "smaller number, earlier process", modify below method "responseSequencerMessages"
                    RM4.responseSequencerMessages(splitMessages);
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

   // @Override
    //listen from sequencer
    /*
    public void run(){
        MulticastSocket listenSocket = null;


        DatagramSocket sendConfirmToSequencer = null;

        try{
            listenSocket = new MulticastSocket(multicastPort);
            listenSocket.joinGroup(InetAddress.getByName(multicastIP));

            sendConfirmToSequencer = new DatagramSocket();

            System.out.println("RM4 UDP Listen Started");
            while (true){
                byte[] buffer = new byte[1000];
                DatagramPacket receiveMessage = new DatagramPacket(buffer, buffer.length);
                listenSocket.receive(receiveMessage);
                System.out.println("message received: " + new String(receiveMessage.getData()));

                //message from FE or Sequencer
                String receiveMessageToString = new String(receiveMessage.getData());
                String rawMessages = receiveMessageToString.trim();

                String[] splitMessages = rawMessages.split("~");

                //from fe's multicast warning messages
                if(splitMessages.length==2){
                    String problemReplicaNumber  = splitMessages[0];
                    String problemType = splitMessages[1];
                    //TODO: add log
                }else{//from sequencer
                    //send replay
                    String confirm = "C";
                    byte[] confirmByte = confirm.getBytes();
                    DatagramPacket replayToSequencer = new DatagramPacket(confirmByte,confirm.length(), InetAddress.getByName(IPPortAddress.SEQUENCER_IP),IPPortAddress.SEQUENCER_UDP_PORT );
                    sendConfirmToSequencer.send(replayToSequencer);

                    //TODO: if need to deal with message "smaller number, earlier process", modify below method "responseSequencerMessages"
                    responseSequencerMessages(splitMessages);
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
    */
    public void restartF(){
        bugF = false;
    }

    public void restartD(){
        bugD = false;
    }



    public void responseSequencerMessages(String[] splitMessages){

        String jobSequenceNumber = splitMessages[0];
        String operationType = splitMessages[1];
        String clientID = splitMessages[2];


        //for the last test case
        if(jobSequenceNumber.equals("5")){
            bugD = true;
        }


        //ArrayList<String> resultArr = new ArrayList<String>();
        String resultStr = "";

        String feedback = ""; //t OR f
        String descriptionMessage = "";
        String readyToSend = "";

        switch (operationType){
            case "add":

                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultStr = mtlServerRM4.addEvent(clientID,splitMessages[3],splitMessages[4],Integer.parseInt(splitMessages[5]) );
                        break;
                    case "OTW":
                        resultStr = otwServerRM4.addEvent(clientID,splitMessages[3],splitMessages[4],Integer.parseInt(splitMessages[5]) );
                        break;
                    case "TOR":
                        resultStr = torServerRM4.addEvent(clientID,splitMessages[3],splitMessages[4],Integer.parseInt(splitMessages[5]) );
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "remove":

                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultStr = mtlServerRM4.removeEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "OTW":
                        resultStr = otwServerRM4.removeEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "TOR":
                        resultStr = torServerRM4.removeEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "list":

                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultStr = mtlServerRM4.listEventAvailability(clientID,splitMessages[3]);
                        break;
                    case "OTW":
                        resultStr = otwServerRM4.listEventAvailability(clientID,splitMessages[3]);
                        break;
                    case "TOR":
                        resultStr = torServerRM4.listEventAvailability(clientID,splitMessages[3]);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "book":

                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultStr = mtlServerRM4.bookEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "OTW":
                        resultStr = otwServerRM4.bookEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "TOR":
                        resultStr = torServerRM4.bookEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "schedule":

                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultStr = mtlServerRM4.getBookingSchedule(clientID);
                        break;
                    case "OTW":
                        resultStr = otwServerRM4.getBookingSchedule(clientID);
                        break;
                    case "TOR":
                        resultStr = torServerRM4.getBookingSchedule(clientID);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "cancel":

                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultStr = mtlServerRM4.cancelEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "OTW":
                        resultStr = otwServerRM4.cancelEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "TOR":
                        resultStr = torServerRM4.cancelEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "swap":

                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultStr = mtlServerRM4.swapEvent(clientID,splitMessages[3],splitMessages[4],splitMessages[5],splitMessages[6]);
                        break;
                    case "OTW":
                        resultStr = otwServerRM4.swapEvent(clientID,splitMessages[3],splitMessages[4],splitMessages[5],splitMessages[6]);
                        break;
                    case "TOR":
                        resultStr = torServerRM4.swapEvent(clientID,splitMessages[3],splitMessages[4],splitMessages[5],splitMessages[6]);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

        }
        String failOrSuccess = resultStr.substring(0,1);
        String afterBug = feedbackOnBugFail(failOrSuccess,bugF);


        StringBuffer sb = new StringBuffer();
        sb.append(jobSequenceNumber + "~");
        sb.append(operationType + "~");
        sb.append(afterBug + "~");
        sb.append(resultStr.substring(2,resultStr.length()));
        readyToSend = sb.toString();
        if(bugD == false){
            sendResultToFrontEnd(readyToSend);
        }
    }

    //method for bug Fail
    public String feedbackOnBugFail(String feedback, boolean bugFStatus) {
        if (!bugFStatus) { //if bugF is false (no fail bug), return the original feedback
            return feedback;
        } else { //if bugF is true (has fail bug), return the contrary
            if (feedback.equals("t")) {
                return "f";
            } else { //feedback.equals("f")
                return "t";
            }
        }
    }

    public void sendResultToFrontEnd(String resultMessage){
        DatagramSocket sendSocket = null;
        System.out.println("sending result to FE");
        try {
            sendSocket = new DatagramSocket(ReplicaManagerUDPPortNumber);
            byte[] resultMessageByte = resultMessage.getBytes();
            InetAddress FEHost =InetAddress.getByName(FEIPNumber);
            DatagramPacket result = new DatagramPacket(resultMessageByte,resultMessage.length(),FEHost,FEUDPPortNumber);
            sendSocket.send(result);
            sendSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
