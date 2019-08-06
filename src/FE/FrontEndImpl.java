package FE;
import FrontEndApp.FrontEnd;
import FrontEndApp.FrontEndHelper;
import FrontEndApp.FrontEndPOA;
import IPPortAddress.IPPortAddress;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class FrontEndImpl extends FrontEndPOA {
    private ORB orb;
    private static final String RM1INFO = IPPortAddress.RM1_IP+":"+IPPortAddress.RM1_UDP_SEND_PORT;
    private static final String RM2INFO = IPPortAddress.RM2_IP+":"+IPPortAddress.RM2_UDP_SEND_PORT;
    private static final String RM3INFO = IPPortAddress.RM3_IP+":"+IPPortAddress.RM3_UDP_SEND_PORT;
    private static final String RM4INFO = IPPortAddress.RM4_IP+":"+IPPortAddress.RM4_UDP_SEND_PORT;

    private int failureCountRM1 = 0;
    private int failureCountRM2 = 0;
    private int failureCountRM3 = 0;
    private int failureCountRM4 = 0;

    public void setORB(ORB orb_val){
        this.orb = orb_val;
    }

    public HashMap<String, String> getRMResponse() {
        HashMap<String, String> responses = new HashMap<>();
        DatagramSocket aSocket = null;
        try {
            int count = 0;
            aSocket = new DatagramSocket(IPPortAddress.FRONTEND_UDP_PORT);
            // set the timeout as 5 seconds
            aSocket.setSoTimeout(5*1000);
            byte[] buffer = new byte[1000];
            while(count<4){
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(response);
                String rawData = new String(response.getData(), 0, response.getLength());
                String senderInfo = response.getAddress().getHostAddress()+":" +response.getPort();
                System.out.println(senderInfo+" "+rawData);
                responses.put(senderInfo, rawData);
                count++;
            }
            aSocket.close();
        }
        catch (SocketTimeoutException e){
            aSocket.close();
            return responses;
            //e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responses;
    }

    public void resetFailureCount(boolean resetRM1, boolean resetRM2, boolean resetRM3, boolean resetRM4){
        if(resetRM1){
            this.failureCountRM1=0;
        }
        if(resetRM2){
            this.failureCountRM2=0;
        }
        if(resetRM3){
            this.failureCountRM3=0;
        }
        if(resetRM4){
            this.failureCountRM4=0;
        }
    }

    public String findDeadReplica(HashMap<String, String> responses){
        System.out.println("Detecting dead Replica...");
        if (!responses.containsKey(RM1INFO)){
            System.out.println("Find dead replica: Replica 1");
            return RM1INFO;
        }
        if(!responses.containsKey(RM2INFO)){
            System.out.println("Find dead replica: Replica 2");
            return RM2INFO;
        }
        if(!responses.containsKey(RM3INFO)){
            System.out.println("Find dead replica: Replica 3");
            return RM3INFO;
        }
        System.out.println("Find dead replica: Replica 4");
        return RM4INFO;
    }

    public String findSFReplica(HashMap<String, String> responses){
        System.out.println("Detecting software failure.....");
        ArrayList<String> tArray = new ArrayList<>();
        ArrayList<String> fArray = new ArrayList<>();

        for(Map.Entry<String, String> response : responses.entrySet()){
            String sender = response.getKey();
            String rawData = response.getValue();
            String[] result = rawData.split("~");
            if(result[2].equals("t")){
                tArray.add(sender);
            }
            else {
                fArray.add(sender);
            }
        }

        if(tArray.size() == 0 || fArray.size() == 0){
            // no SF
            System.out.println("No software failure");
            resetFailureCount(true,true,true,true);
            return null;
        }
        String sfReplica = null;
        if(tArray.size() > fArray.size()){
            sfReplica = fArray.get(0);
        }
        else {
            sfReplica = tArray.get(0);
        }
        String sfReplicaNumber = "";
        switch (sfReplica){
            case RM1INFO:
                resetFailureCount(false,true,true,true);
                this.failureCountRM1++;
                sfReplicaNumber="1";
                break;
            case RM2INFO:
                resetFailureCount(true,false,true,true);
                this.failureCountRM2++;
                sfReplicaNumber="2";
                break;
            case RM3INFO:
                resetFailureCount(true,true,false,true);
                this.failureCountRM3++;
                sfReplicaNumber="3";
                break;
            case RM4INFO:
                resetFailureCount(true,true,true,false);
                this.failureCountRM4++;
                sfReplicaNumber="4";
                break;
        }
        System.out.println("Find software failure: Replica "+sfReplicaNumber);
        return sfReplica;
    }

    public String checkIf3SF(){
        System.out.println("Checking if 3 continuous sfs....");
        if(failureCountRM1 == 3){
            System.out.println("Find Replica 1 has 3 sfs");
            return RM1INFO;
        }
        if(failureCountRM2 == 3){
            System.out.println("Find Replica 2 has 3 sfs");
            return RM2INFO;
        }
        if(failureCountRM3 == 3){
            System.out.println("Find Replica 3 has 3 sfs");
            return RM3INFO;
        }
        if(failureCountRM4 == 3){
            System.out.println("Find Replica 4 has 3 sfs");
            return RM4INFO;
        }
        System.out.println("No such Replica");
        return null;
    }

    public void restartReplica(String replicaInfo, String restartCommand){
        if(replicaInfo == null){
            return;
        }
        int replicaNumber = getReplicaNumber(replicaInfo);
        System.out.println("Ask Replica to restart: Replica "+getReplicaNumber(replicaInfo));

        String replicaIP = "";
        int replicaPort = 0;

        switch (replicaNumber){
            case 1:
                replicaIP = IPPortAddress.RM1_IP;
                replicaPort = IPPortAddress.RM1_UDP_PORT;
                break;
            case 2:
                replicaIP = IPPortAddress.RM2_IP;
                replicaPort = IPPortAddress.RM2_UDP_PORT;
                break;
            case 3:
                replicaIP = IPPortAddress.RM3_IP;
                replicaPort = IPPortAddress.RM3_UDP_PORT;
                break;
            case 4:
                replicaIP = IPPortAddress.RM4_IP;
                replicaPort = IPPortAddress.RM4_UDP_PORT;
                break;
        }

        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] msg = restartCommand.getBytes();
            InetAddress aHost = InetAddress.getByName(replicaIP);
            DatagramPacket request = new DatagramPacket(msg, msg.length, aHost, replicaPort);
            socket.send(request);
            socket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getMajorityResponseMsg(HashMap<String, String> responses, String sfReplica){
        String msg = "";
        for(Map.Entry<String, String> response : responses.entrySet()){
            if(!response.getKey().equals(sfReplica)){
                String rawData = response.getValue();
                String[] data = rawData.split("~");
                msg = data[3];
                break;
            }
        }
        System.out.println("Majority response: "+msg);
        return msg;
    }

    public void multicastMsgToRMs(String message){
        System.out.println("Multicast msg to RMs: "+message);
        try{
            DatagramSocket socket = new DatagramSocket();
            InetAddress group = InetAddress.getByName(IPPortAddress.MULTICAST_IP);
            byte[] msg = message.getBytes();
            DatagramPacket packet = new DatagramPacket(msg, msg.length, group, IPPortAddress.MULTICAST_PORT);
            socket.send(packet);
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getReplicaNumber(String replicaInfo){
        if(RM1INFO.equals(replicaInfo)){
            return 1;
        }
        if(RM2INFO.equals(replicaInfo)){
            return 2;
        }
        if(RM3INFO.equals(replicaInfo)){
            return 3;
        }
        return 4;
    }

    public String sendRequestToSequencer(String message){
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();

            byte[] msg = message.getBytes();
            InetAddress aHost = InetAddress.getByName(IPPortAddress.SEQUENCER_IP);
            DatagramPacket request = new DatagramPacket(msg, msg.length, aHost, IPPortAddress.SEQUENCER_UDP_PORT);
            socket.send(request);
            // debug info
            System.out.println("send request "+ message);

            HashMap<String, String> responses = getRMResponse();

            if(responses.size() < 4){
                String deadReplicaInfo = findDeadReplica(responses);
                int replicaNumber = getReplicaNumber(deadReplicaInfo);
                String warning = replicaNumber+"~d";
                multicastMsgToRMs(warning);
                restartReplica(deadReplicaInfo, "R~d");
            }
            String sfReplica = findSFReplica(responses);

            if(sfReplica != null){
                int replicaNumber = getReplicaNumber(sfReplica);
                String warning = replicaNumber+"~f";
                multicastMsgToRMs(warning);
                restartReplica(checkIf3SF(),"R~f");
            }
            socket.close();
            return getMajorityResponseMsg(responses, sfReplica);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Error";
    }


    public static void main(String[] args){
        String[] params = {"-ORBInitialPort", "1050",  "-ORBInitialHost", "localhost"};
        FrontEndImpl frontEnd = new FrontEndImpl();
        try{
            ORB orb = ORB.init(params, null);
            //get reference to rootpoa
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

            rootpoa.the_POAManager().activate();

            //create servant and register with orb
            frontEnd.setORB(orb);

            //get object reference to a CORBA reference
            org.omg.CORBA.Object ref= rootpoa.servant_to_reference(frontEnd);

            //cast to a corba reference
            FrontEnd href= FrontEndHelper.narrow(ref);

            //get the rootnaming context
            org.omg.CORBA.Object  objRef= orb.resolve_initial_references("NameService");

            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            NameComponent path[]= ncRef.to_name("FRONTEND");
            ncRef.rebind(path,href);

            System.out.println("FrontEnd ready and waiting ...");

            // wait for invocations from clients
            for (;;) {
                orb.run();
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String addEvent(String managerID, String eventID, String eventType, int bookingCapacity) {
        synchronized (this){
            String message = "add~"+managerID+"~"+eventID+"~"+eventType+"~"+bookingCapacity;
            return sendRequestToSequencer(message);
        }
    }

    @Override
    public String removeEvent(String managerID, String eventID, String eventType) {
        String message = "remove~"+managerID+"~"+eventID+"~"+eventType;
        return sendRequestToSequencer(message);
    }

    @Override
    public String listEventAvailability(String managerID, String eventType) {
        String message = "list~"+managerID+"~"+eventType;
        return sendRequestToSequencer(message);
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) {
        String message = "book~"+customerID+"~"+eventID+"~"+eventType;
        return sendRequestToSequencer(message);
    }

    @Override
    public String getBookingSchedule(String customerID) {
        String message = "schedule~"+customerID;
        return sendRequestToSequencer(message);
    }

    @Override
    public String cancelEvent(String customerID, String eventID, String eventType) {
        String message = "cancel~"+customerID+"~"+eventID+"~"+eventType;
        return sendRequestToSequencer(message);

    }

    @Override
    public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) {
        String message = "swap~"+customerID+"~"+newEventID+"~"+newEventType+"~"+oldEventID+"~"+oldEventType;
        return sendRequestToSequencer(message);
    }


}
