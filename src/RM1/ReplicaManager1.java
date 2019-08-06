package RM1;

import IPPortAddress.IPPortAddress;

import Sequencer.Sequencer;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ReplicaManager1 extends Thread{


    private static MTLServerRM1 mtlServerRM1;
    private static OTWServerRM1 otwServerRM1;
    private static TORServerRM1 torServerRM1;

    private String FEIPNumber = IPPortAddress.FRONTEND_IP;
    private int FEUDPPortNumber = IPPortAddress.FRONTEND_UDP_PORT;

    private String multicastIP = IPPortAddress.MULTICAST_IP;
    private int multicastPort = IPPortAddress.MULTICAST_PORT;

    private int ReplicaManagerUDPPortNumber = IPPortAddress.RM1_UDP_SEND_PORT;

    private int biggestSequenceNo = 0;

    private boolean bugF = false;
    private boolean bugD = false;

    public ReplicaManager1() throws Exception {
        //start all three servers
        mtlServerRM1 = new MTLServerRM1();
        otwServerRM1 = new OTWServerRM1();
        torServerRM1 = new TORServerRM1();
    }

    public static void main(String[] args) throws Exception {
        ReplicaManager1 rm1 = new ReplicaManager1();
        rm1.start();
        ListenThreadFromFE listenThreadFromFE = new ListenThreadFromFE(rm1);
        listenThreadFromFE.start();

    }

    @Override
    //listen from sequencer
    public void run(){
        MulticastSocket listenSocket = null;

        DatagramSocket sendConfirmToSequencer = null;

        try{
            listenSocket = new MulticastSocket(multicastPort);
            listenSocket.joinGroup(InetAddress.getByName(multicastIP));

            sendConfirmToSequencer = new DatagramSocket();

            System.out.println("RM1 UDP Listen Started");
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

        ArrayList<String> resultArr = new ArrayList<String>();
        String resultStr = "";

        String feedback = ""; //t OR f
        String descriptionMessage = "";
        String readyToSend = "";

        switch (operationType){
            case "add":
                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultArr = mtlServerRM1.serverOBJ.addEvent(clientID,splitMessages[3],splitMessages[4],Integer.parseInt(splitMessages[5]) );
                        break;
                    case "OTW":
                        resultArr = otwServerRM1.serverOBJ.addEvent(clientID,splitMessages[3],splitMessages[4],Integer.parseInt(splitMessages[5]) );
                        break;
                    case "TOR":
                        resultArr = torServerRM1.serverOBJ.addEvent(clientID,splitMessages[3],splitMessages[4],Integer.parseInt(splitMessages[5]) );
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "remove":
                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultArr = mtlServerRM1.serverOBJ.removeEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "OTW":
                        resultArr = otwServerRM1.serverOBJ.removeEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "TOR":
                        resultArr = torServerRM1.serverOBJ.removeEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "list":
                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultArr = mtlServerRM1.serverOBJ.listEventAvailability(clientID,splitMessages[3]);
                        break;
                    case "OTW":
                        resultArr = otwServerRM1.serverOBJ.listEventAvailability(clientID,splitMessages[3]);
                        break;
                    case "TOR":
                        resultArr = torServerRM1.serverOBJ.listEventAvailability(clientID,splitMessages[3]);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "book":
                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultArr = mtlServerRM1.serverOBJ.bookEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "OTW":
                        resultArr = otwServerRM1.serverOBJ.bookEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "TOR":
                        resultArr = torServerRM1.serverOBJ.bookEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "schedule":
                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultArr = mtlServerRM1.serverOBJ.getBookingSchedule(clientID);
                        break;
                    case "OTW":
                        resultArr = otwServerRM1.serverOBJ.getBookingSchedule(clientID);
                        break;
                    case "TOR":
                        resultArr = torServerRM1.serverOBJ.getBookingSchedule(clientID);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "cancel":
                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultStr = mtlServerRM1.serverOBJ.cancelEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "OTW":
                        resultStr = otwServerRM1.serverOBJ.cancelEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    case "TOR":
                        resultStr = torServerRM1.serverOBJ.cancelEvent(clientID,splitMessages[3],splitMessages[4]);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

            case "swap":
                switch (clientID.substring(0, 3)) {
                    case "MTL":
                        resultArr = mtlServerRM1.serverOBJ.swapEvent(clientID,splitMessages[3],splitMessages[4],splitMessages[5],splitMessages[6]);
                        break;
                    case "OTW":
                        resultArr = otwServerRM1.serverOBJ.swapEvent(clientID,splitMessages[3],splitMessages[4],splitMessages[5],splitMessages[6]);
                        break;
                    case "TOR":
                        resultArr = torServerRM1.serverOBJ.swapEvent(clientID,splitMessages[3],splitMessages[4],splitMessages[5],splitMessages[6]);
                        break;
                    default:
                        System.out.println("wrong");
                        break;
                }
                break;

        }

        /* analyse result and give the final result to readyToSend
        RM Response format: (RM to FE)
        Exp: 1~book~t~Book Successfully
        <Sequence Number>~<Operation Type>~<Result (t/f)>~<Description Message>*/
        if (operationType.equals("add")) { //return info stored in resultArr
            operationType = "add";
            if (resultArr.get(0).equals("Added")){
                feedback = feedbackOnBugFail("t", bugF);
                descriptionMessage = resultArr.get(1);
            } else {
                feedback = feedbackOnBugFail("f", bugF);
                descriptionMessage = resultArr.get(1);
            }
        } else if (operationType.equals("remove")){
            operationType = "remove";
            if (resultArr.get(0).equals("Success")){
                feedback = feedbackOnBugFail("t", bugF);
                descriptionMessage = "Success. " + resultArr.get(1);
            } else {
                feedback = feedbackOnBugFail("f", bugF);
                descriptionMessage = "Fail. " + resultArr.get(1);
            }
        } else if (operationType.equals("list")){
            operationType = "list";
            feedback = feedbackOnBugFail("t", bugF);
            StringBuffer sbList = new StringBuffer();
            for (int i = 0; i<resultArr.size()-1; i++){
                sbList.append(resultArr.get(i) + "\n");
            }
            sbList.append(resultArr.get(resultArr.size()-1)); //this is to avoid a \n at the last line - Lin
            descriptionMessage = sbList.toString();
        } else if (operationType.equals("book")){
            operationType = "book";
            if (resultArr.get(0).equals("Success")){
                feedback = feedbackOnBugFail("t", bugF);
                descriptionMessage = resultArr.get(1);
            } else {
                feedback = feedbackOnBugFail("f", bugF);
                descriptionMessage = "Fail. " + resultArr.get(1);
            }
        } else if (operationType.equals("schedule")){
            operationType = "schedule";
            StringBuffer sbSchedule = new StringBuffer();
            
            if (resultArr.size()==0) {
            	feedback = feedbackOnBugFail("f", bugF);
                descriptionMessage = "Fail. There is no booking record for this customer.";
			} else {
				feedback = feedbackOnBugFail("t", bugF);
				String lineFormated = String.format("%-15s %-18s %-15s", "City", "Event Type", "Event ID");
				sbSchedule.append(lineFormated + "\n");
				
				for (String s : resultArr) {
					String subStringCity = s.trim().substring(1, 4);
					String subStringEventType = s.trim().substring(0, 1);
					String eventID = s.trim().substring(1);

					if (subStringCity.equals("MTL")) {
						if (subStringEventType.contentEquals("C")) {
							lineFormated = String.format("%-15s %-18s %-15s", "Montreal", "Conference", eventID);
							sbSchedule.append(lineFormated + "\n");
						} else if (subStringEventType.contentEquals("S")) {
							lineFormated = String.format("%-15s %-18s %-15s", "Montreal", "Seminar", eventID);
							sbSchedule.append(lineFormated + "\n");
						} else if (subStringEventType.contentEquals("T")) {
							lineFormated = String.format("%-15s %-18s %-15s", "Montreal", "Trade Show", eventID);
							sbSchedule.append(lineFormated + "\n");
						}
					} else if (subStringCity.equals("TOR")) {
						if (subStringEventType.contentEquals("C")) {
							lineFormated = String.format("%-15s %-18s %-15s", "Toronto", "Conference", eventID);
							sbSchedule.append(lineFormated + "\n");
						} else if (subStringEventType.contentEquals("S")) {
							lineFormated = String.format("%-15s %-18s %-15s", "Toronto", "Seminar", eventID);
							sbSchedule.append(lineFormated + "\n");
						} else if (subStringEventType.contentEquals("T")) {
							lineFormated = String.format("%-15s %-18s %-15s", "Toronto", "Trade Show", eventID);
							sbSchedule.append(lineFormated + "\n");
						}
					} else if (subStringCity.equals("OTW")) {
						if (subStringEventType.contentEquals("C")) {
							lineFormated = String.format("%-15s %-18s %-15s", "Ottawa", "Conference", eventID);
							sbSchedule.append(lineFormated + "\n");
						} else if (subStringEventType.contentEquals("S")) {
							lineFormated = String.format("%-15s %-18s %-15s", "Ottawa", "Seminar", eventID);
							sbSchedule.append(lineFormated + "\n");
						} else if (subStringEventType.contentEquals("T")) {
							lineFormated = String.format("%-15s %-18s %-15s", "Ottawa", "Trade Show", eventID);
							sbSchedule.append(lineFormated + "\n");
						}
					}
				}
				descriptionMessage = sbSchedule.toString();
			}

        } else if (operationType.equals("cancel")){ //for cancelEvent, return info stored in resultStr, instead of resultArr
            operationType = "cancel";
            if (resultStr.equals("Success")){
                feedback = feedbackOnBugFail("t", bugF);
                descriptionMessage = "Successfully cancelled an event.";
            } else {
                feedback = feedbackOnBugFail("f", bugF);
                if (resultStr.equals("EventNotExist")){
                    descriptionMessage = "Fail. " + "This event doesn't exist.";
                } else if (resultStr.equals("CustomerNeverBooked")) {
                    descriptionMessage = "Fail. " + "This customer has never booked before.";
                } else if (resultStr.equals("ThisCustomerHasNotBookedThis")) {
                    descriptionMessage = "Fail. " + "This customer has never booked this event." ;
                } else if (resultStr.equals("Capacity Error")) {
                    descriptionMessage = "Fail. Capacity Error." ;
                } else  {
                    descriptionMessage = "Fail." ;
                }
            }
        } else if (operationType.equals("swap")){
            operationType = "swap";
            if (resultArr.get(0).equals("Success") && resultArr.get(1).equals("Success") ){ //both book and cancel are successful
                feedback = feedbackOnBugFail("t", bugF);
                descriptionMessage = "Successfully swaped two events.";
            } else {
                feedback = feedbackOnBugFail("f", bugF);
                descriptionMessage = "Failed in swapping two events.";
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append(jobSequenceNumber + "~");
        sb.append(operationType + "~");
        sb.append(feedback + "~");
        sb.append(descriptionMessage);
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
