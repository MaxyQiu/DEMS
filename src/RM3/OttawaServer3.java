package RM3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.HashMap;
import java.util.Map;
import java.util.*;

public class OttawaServer3 implements ServerInterface, Runnable {

    private HashMap<String, HashMap<String, Event>> database;
    private HashMap<String, HashMap<String, int[]>> bookingCount;
    public final static String NAME = "OTTAWA";

    private final static int UDPPORT = OUDPPORT;

    private final static String shortName = "OTW";

    public OttawaServer3()  {
        this.database = new HashMap<>();
        this.bookingCount = new HashMap<>();

        database.put("Conferences", new HashMap<>());
        database.put("Seminars", new HashMap<>());
        database.put("Trade Shows", new HashMap<>());

        // add some default event
        /*database.get("Seminars").put("OTWE110519", new Event("OTWE110519", "Seminars", 5));
        database.get("Conferences").put("OTWM120519", new Event("OTWE120519", "Conferences", 9));
        database.get("Seminars").put("OTWE300519", new Event("OTWE300519", "Seminars", 2));
        database.get("Trade Shows").put("OTWA110619", new Event("OTWA110619", "Trade Shows", 100));
    */
    }

    public void run() {
        try{
            OttawaServer3 server = this;
            ServerLog.createLog(NAME);

            System.out.println("Server "+NAME+" starts");
            ServerLog.log(NAME,"Server "+NAME+" starts ");

            DatagramSocket aSocket = new DatagramSocket(UDPPORT);
            byte[] buffer = new byte[1000];
            System.out.println("Server "+NAME+" starts (UDP PORT "+UDPPORT+")");
            ServerLog.log(NAME,"Server "+NAME+" starts (UDP PORT "+UDPPORT+")");


            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                String data = new String(request.getData(), 0, request.getLength());

                String out = "Receive UDP request: "+data;

                System.out.println(out);
                ServerLog.log(NAME,out);

                String[] dataA = data.split("-");
                String responseInfo = "";

                if(dataA[0].equals("list")){
                    responseInfo = server.listMyEventAvailability(dataA[1]);
                }
                else if(dataA[0].equals("schedule")){
                    responseInfo = server.getMyBookingEvent(dataA[1]);
                }
                else if(dataA[0].equals("cancel")){
                    responseInfo = server.cancelMyEvent(dataA[1],dataA[2],dataA[3]);
                }
                else if(dataA[0].equals("book")){
                    responseInfo = server.bookMyEvent(dataA[1],dataA[2],dataA[3]);
                }
                else if(dataA[0].equals("remove")){
                    server.removeInBookCount(dataA[3], Integer.parseInt(dataA[1]), dataA[2]);
                    continue;
                }

                byte[] response = responseInfo.getBytes();

                out = "Send UDP response \n"+responseInfo;
                System.out.println(out);
                ServerLog.log(NAME, out);

                DatagramPacket reply = new DatagramPacket(response,
                        response.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
            }


        }
        catch (Exception e){
            System.out.println("Server fails");
            ServerLog.log(NAME, "Server fails");
        }
    }

    @Override
    public String addEvent(String managerID, String eventID, String eventType, int bookingCapacity) {

        // Hash map is not thread safe
        synchronized (this){
            System.out.println("Manager "+managerID+ "wants to add event: "+eventID+" "+bookingCapacity+ " "+ eventType);
            ServerLog.log(NAME,"Manager "+managerID+ "wants to add event: "+eventID+" "+bookingCapacity+ " "+ eventType);

            if(!eventID.substring(0,3).equals(shortName)){

                String msg = "f~Cannot add event in other city";

                System.out.println(msg);
                ServerLog.log(NAME, msg);

                return msg;
            }

            // check if there is the same event
            HashMap<String, Event> eventTypeDB = this.database.get(eventType);
            if(eventTypeDB.containsKey(eventID)){
                Event event = eventTypeDB.get(eventID);
                event.setCapacity(event.getCapacity()+bookingCapacity);

                String msg = "f~Event already exists, update this event's capacity to "+event.getCapacity();

                System.out.println(msg);
                ServerLog.log(NAME, msg);

                return msg;
            }

            Event event = new Event(eventID,eventType,bookingCapacity);
            eventTypeDB.put(eventID,event);

            String msg = "t~Added event successfully";

            System.out.println(msg);
            ServerLog.log(NAME, msg);

            return msg;
        }
    }

    @Override
    public String removeEvent(String managerID, String eventID, String eventType) {
        synchronized (this){
            System.out.println("Manager "+managerID+ "wants to remove event: "+eventID+" " + eventType);
            ServerLog.log(NAME,"Manager "+managerID+ "wants to remove event: "+eventID+" " + eventType);

            HashMap<String, Event> eventTypeDB = this.database.get(eventType);
            if(eventTypeDB.containsKey(eventID)){
                ArrayList<String> customerList = eventTypeDB.get(eventID).getBookedCustomerList();

                StringBuilder myListSB = new StringBuilder();
                StringBuilder list1SB = new StringBuilder();
                StringBuilder list2SB = new StringBuilder();
                for (String customerID: customerList) {
                    if(customerID.substring(0,3).equals(shortName)){

                        myListSB.append(customerID);
                        myListSB.append(",");
                    }
                    else if(customerID.substring(0,3).equals("MTL")){

                        list1SB.append(customerID);
                        myListSB.append(",");
                    }
                    else{

                        list2SB.append(customerID);
                        myListSB.append(",");
                    }
                }
                int month = Integer.parseInt(eventID.substring(6,8))-1;

                if(myListSB.toString().length()!=0){
                    removeInBookCount(myListSB.toString(), month, shortName);
                }
                if(list1SB.toString().length()!=0){
                    DatagramSocket aSocket1;
                    try{
                        aSocket1 = new DatagramSocket();
                        byte[] msg = ("remove-"+month+"-"+shortName+"-"+list1SB.toString()).getBytes();
                        InetAddress aHost = InetAddress.getByName("localhost");
                        DatagramPacket request1 = new DatagramPacket(msg,msg.length, aHost, MUDPPORT);
                        aSocket1.send(request1);
                    }
                    catch (Exception e){
                        return "f~UDP Error";
                    }
                }
                if(list2SB.toString().length()!=0){
                    DatagramSocket aSocket1;
                    try{
                        aSocket1 = new DatagramSocket();
                        byte[] msg = ("remove-"+month+"-"+shortName+"-"+list2SB.toString()).getBytes();
                        InetAddress aHost = InetAddress.getByName("localhost");
                        DatagramPacket request1 = new DatagramPacket(msg,msg.length, aHost, TUDPPORT);
                        aSocket1.send(request1);
                    }
                    catch (Exception e){
                        return "f~UDP Error";
                    }
                }
                eventTypeDB.remove(eventID);
                // to do - need log
                String msg = "t~Successfully remove the event: "+eventID;

                System.out.println(msg);
                ServerLog.log(NAME, msg);
                return msg;
            }
            String msg = "f~No such event";
            System.out.println(msg);
            ServerLog.log(NAME, msg);
            return msg;
        }
    }

    public void removeInBookCount(String idList, int month, String loc){
        synchronized (this){
            String[] customerIDs = idList.split(",");
            for (String customerID : customerIDs) {
                this.bookingCount.get(customerID).get(loc)[month] -= 1;
            }
        }
    }

    @Override
    public String listEventAvailability(String managerID, String eventType)  {

        System.out.println(managerID+" requests event availability for type "+eventType);
        ServerLog.log(NAME, managerID+" requests event availability for type "+eventType);

        DatagramSocket aSocket1 = null;
        DatagramSocket aSocket2 = null;

        String list = eventType+"\n";
        list+=listMyEventAvailability(eventType);
        list+="\n";

        try{
            aSocket1 = new DatagramSocket();
            aSocket2 = new DatagramSocket();
            byte[] msg = ("list-"+eventType).getBytes();

            // send udp request to other servers
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request1 = new DatagramPacket(msg,msg.length, aHost, MUDPPORT);
            DatagramPacket request2 = new DatagramPacket(msg,msg.length, aHost, TUDPPORT);

            aSocket1.send(request1);
            aSocket2.send(request2);

            ServerLog.log(NAME,"Send list availability request for type "+eventType+" to port "+request1.getPort());
            ServerLog.log(NAME,"Send list availability request for type "+eventType+" to port "+request2.getPort());


            byte[] buffer = new byte[1000];
            byte[] buffer2 = new byte[1000];

            DatagramPacket reply1 = new DatagramPacket(buffer, buffer.length);
            DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);

            aSocket1.receive(reply1);
            aSocket2.receive(reply2);

            String data1 = new String(reply1.getData(), 0, reply1.getLength());
            ServerLog.log(NAME, "Receive response \n"+data1+" from "+reply1.getPort());

            String data2 = new String(reply2.getData(), 0, reply2.getLength());
            ServerLog.log(NAME, "Receive response \n"+data2+" from "+reply2.getPort());

            list += data1;
            list += "\n";

            list += data2;

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println("Send back to client \n"+list);
        ServerLog.log(NAME, "Send back to client \n"+list);
        return "t~"+list;
    }

    public String listMyEventAvailability(String eventType)  {
        synchronized (this){
            HashMap<String, Event> eventTypeDB = this.database.get(eventType);
            StringBuilder list = new StringBuilder();
            list.append(NAME+":\n");
            for(Map.Entry<String, Event> entry : eventTypeDB.entrySet()) {
                String id = entry.getKey();
                Event event = entry.getValue();

                list.append(id);
                list.append(" ");
                list.append(event.getAvailability());
                list.append("\n");

            }

            // to do - UDP to get lists from other servers
            return list.toString();
        }
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType)  {

        synchronized (this) {
            System.out.println("Customer "+customerID+ " wants to book event "+eventID+" "+eventType);
            ServerLog.log(NAME,"Customer "+customerID+ " wants to book event "+eventID+" "+eventType);


            int month = Integer.parseInt(eventID.substring(6, 8)) - 1;
            String result = "";
            if (!eventID.substring(0, 3).equals(shortName)) {

                // check if the customer reach the limits

                int count = 0;

                if (bookingCount.containsKey(customerID)) {
                    count += bookingCount.get(customerID).get("TOR")[month];
                    count += bookingCount.get(customerID).get("MTL")[month];
                }

                if (count >= 3) {
                    String msg = "f~This customer reaches the monthly limits for booking event in other cities";
                    System.out.println(msg);
                    ServerLog.log(NAME,msg);
                    return msg;
                }

                DatagramSocket aSocket;

                try {
                    aSocket = new DatagramSocket();

                    byte[] msg = ("book-" + customerID + "-" + eventID + "-" + eventType).getBytes();

                    // send udp request to other servers
                    InetAddress aHost = InetAddress.getByName("localhost");
                    DatagramPacket request;
                    if (eventID.substring(0, 3).equals("TOR")) {
                        request = new DatagramPacket(msg, msg.length, aHost, TUDPPORT);

                    } else if (eventID.substring(0, 3).equals("MTL")) {
                        request = new DatagramPacket(msg, msg.length, aHost, MUDPPORT);
                    } else {
                        return "f~Invalid Input";
                    }

                    String out = "Send UDP book request "+msg.toString();
                    aSocket.send(request);

                    System.out.println(out);
                    ServerLog.log(NAME, out);

                    byte[] buffer = new byte[1000];

                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(reply);
                    String response = new String(reply.getData(), 0, reply.getLength());

                    ServerLog.log(NAME,"Get UDP response "+response);

                    if (response.equals("Book successfully")) {
                        result = "t~"+response;
                        if (!this.bookingCount.containsKey(customerID)) {

                            HashMap<String, int[]> cityCount = new HashMap<>();
                            cityCount.put("MTL", new int[12]);
                            cityCount.put("TOR", new int[12]);
                            cityCount.put("OTW", new int[12]);
                            this.bookingCount.put(customerID, cityCount);

                        }
                        int[] bookingArray = this.bookingCount.get(customerID).get(eventID.substring(0, 3));
                        bookingArray[month] += 1;
                    }
                    else{
                        result = "f~"+response;
                    }
                    System.out.println(result);
                    ServerLog.log(NAME, "Send back to client "+result);

                    return result;

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            String response = bookMyEvent(customerID, eventID, eventType);

            if (response.equals("Book successfully")) {
                result = "t~"+response;
                if (!this.bookingCount.containsKey(customerID)) {

                    HashMap<String, int[]> cityCount = new HashMap<>();
                    cityCount.put("MTL", new int[12]);
                    cityCount.put("TOR", new int[12]);
                    cityCount.put("OTW", new int[12]);
                    this.bookingCount.put(customerID, cityCount);

                }
                int[] bookingArray = this.bookingCount.get(customerID).get(eventID.substring(0, 3));
                bookingArray[month] += 1;
            }
            else{
                result = "f~"+response;
            }

            System.out.println(response);
            ServerLog.log(NAME, "Send back to client "+response);
            return result;
        }

    }

    public String bookMyEvent(String customerID, String eventID, String eventType){
        synchronized (this){
            HashMap<String, Event> eventTypeDB = this.database.get(eventType);

            Event event = eventTypeDB.get(eventID);

            if(event!=null){

                if(event.isBookedBy(customerID)){
                    return "This event has already been booked by the customer.";
                }
                if(event.book(customerID)){
                    return "Book successfully";
                }
                else{
                    return "Sorry, this event is full";
                }
            }
            return "Sorry, there is no such event";
        }
    }

    @Override
    public String getBookingSchedule(String customerID)  {
        System.out.println(customerID+" requests book schedule");
        ServerLog.log(NAME, customerID+" requests book schedule");

        DatagramSocket aSocket1 = null;
        DatagramSocket aSocket2 = null;

        String list = "";
        list += getMyBookingEvent(customerID);
        list+="\n";

        try{
            aSocket1 = new DatagramSocket();
            aSocket2 = new DatagramSocket();
            byte[] msg = ("schedule-"+customerID).getBytes();

            // send udp request to other servers
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request1 = new DatagramPacket(msg,msg.length, aHost, MUDPPORT);
            DatagramPacket request2 = new DatagramPacket(msg,msg.length, aHost, TUDPPORT);

            ServerLog.log(NAME,"Send book schedule request to port "+request1.getPort());
            ServerLog.log(NAME,"Send book schedule request to port "+request2.getPort());

            aSocket1.send(request1);
            aSocket2.send(request2);

            byte[] buffer = new byte[1000];
            byte[] buffer2 = new byte[1000];

            DatagramPacket reply1 = new DatagramPacket(buffer, buffer.length);
            DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);

            aSocket1.receive(reply1);
            aSocket2.receive(reply2);

            String data1 = new String(reply1.getData(), 0, reply1.getLength());
            ServerLog.log(NAME, "Receive response: \n"+data1+" from "+reply1.getPort());

            String data2 = new String(reply2.getData(), 0, reply2.getLength());
            ServerLog.log(NAME, "Receive response: \n"+data2+" from "+reply2.getPort());

            list += data1;
            list += "\n";

            list += data2;

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println("Send back to client \n"+list);
        ServerLog.log(NAME, "Send back to client \n"+list);
        return "t~"+list;

    }

    public String getMyBookingEvent(String customerID){
        synchronized (this){
            StringBuilder list = new StringBuilder();
            list.append(NAME + ":\n");
            for(Map.Entry<String, HashMap<String, Event>> entryMap : this.database.entrySet()) {

                HashMap<String, Event> map = entryMap.getValue();

                for(Map.Entry<String, Event> entry : map.entrySet()){
                    String id = entry.getKey();
                    Event event = entry.getValue();

                    if(event.isBookedBy(customerID)){
                        list.append(id);
                        list.append(" ");
                        list.append(event.getEventType());
                        list.append("\n");
                    }
                }
            }

            // to do - UDP to get lists from other servers
            return list.toString();
        }
    }


    @Override
    public String cancelEvent(String customerID, String eventID, String eventType)  {
        synchronized (this) {
            System.out.println("Customer "+customerID+ " wants to cancel event "+eventID+" "+eventType);
            ServerLog.log(NAME,"Customer "+customerID+ " wants to cancel event "+eventID+" "+eventType);

            int month = Integer.parseInt(eventID.substring(6, 8)) - 1;

            String result = "";
            // see if need udp
            if (!eventID.substring(0, 3).equals(shortName)) {

                DatagramSocket aSocket;

                try {
                    aSocket = new DatagramSocket();

                    byte[] msg = ("cancel-" + customerID + "-" + eventID + "-" + eventType).getBytes();

                    // send udp request to other servers
                    InetAddress aHost = InetAddress.getByName("localhost");
                    DatagramPacket request;
                    if (eventID.substring(0, 3).equals("TOR")) {
                        request = new DatagramPacket(msg, msg.length, aHost, TUDPPORT);
                    } else if (eventID.substring(0, 3).equals("MTL")) {
                        request = new DatagramPacket(msg, msg.length, aHost, MUDPPORT);
                    } else {
                        return "f~Invalid Input";
                    }

                    String out = "Send UDP cancel request to "+request.getPort()+"\n"+msg.toString();
                    System.out.println(out);
                    ServerLog.log(NAME, out);
                    aSocket.send(request);

                    byte[] buffer = new byte[1000];

                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

                    aSocket.receive(reply);

                    String response = new String(reply.getData(), 0, reply.getLength());

                    ServerLog.log(NAME,"Get UDP response "+response);

                    if (response.equals("Cancel successfully")) {
                        result = "t~"+response;
                        int[] bookingArray = this.bookingCount.get(customerID).get(eventID.substring(0, 3));
                        bookingArray[month] -= 1;
                    }
                    else{
                        result = "f~"+response;
                    }

                    System.out.println(response);
                    ServerLog.log(NAME, "Send back to client "+response);

                    return result;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            String response = cancelMyEvent(customerID, eventID, eventType);
            if (response.equals("Cancel successfully")) {
                result = "t~"+response;
                int[] bookingArray = this.bookingCount.get(customerID).get(eventID.substring(0, 3));
                bookingArray[month] -= 1;
            }
            else{
                result = "f~"+response;
            }

            System.out.println(response);
            ServerLog.log(NAME, "Send back to client "+response);
            return result;
        }
    }
    public String cancelMyEvent(String customerID, String eventID, String eventType){
        synchronized (this){
            HashMap<String, Event> eventTypeDB = this.database.get(eventType);
            Event event = eventTypeDB.get(eventID);
            if(eventID == null){
                return "There is no such event";
            }

            if(event.cancel(customerID)){
                return "Cancel successfully";
            }

            return "You has not booked this event";
        }
    }

    @Override
    public String swapEvent(String customerID,String newEventID, String newEventType, String oldEventID, String oldEventType) {
        synchronized (this) {

            System.out.println("Customer "+customerID+ " wants to swap event "+oldEventID +"-"+oldEventType+" with "+newEventID+"-"+newEventType);
            ServerLog.log(NAME, "Customer "+customerID+ " wants to swap event "+oldEventID +"-"+oldEventType+" with "+newEventID+"-"+newEventType);
            String listOE=getBookingSchedule(customerID);

            boolean cancelOldevent=false;
            Scanner scan= new Scanner(listOE);
            //Check the old event
            while(scan.hasNextLine())
            {
                String line = scan.nextLine();

                if(line.length()>11) {
                    System.out.println(line);
                    if (line.equals(oldEventID+" "+oldEventType)) {
                        cancelOldevent = true;
                        break;
                    }

                }
            }

            scan.close();

            if(cancelOldevent) {

                // dummy cancel here for release the monthly limits
                int month = Integer.parseInt(oldEventID.substring(6, 8)) - 1;
                int[] bookingArray = this.bookingCount.get(customerID).get(oldEventID.substring(0, 3));
                bookingArray[month] -= 1;

                String responseBE = bookEvent(customerID, newEventID, newEventType);
                String[] responseArray = responseBE.split("~");

                String responseDescription = responseArray[1];

                // recover the monthly limits record
                bookingArray[month] += 1;

                if(responseBE.equals("t~Book successfully")) {
                    cancelEvent(customerID, oldEventID, oldEventType);
                    ServerLog.log(NAME, "t~Swap successfully!");
                    return "t~Swap successfully!";
                }
                ServerLog.log(NAME, "f~Swap failed cuz: "+responseDescription);
                return "f~Swap failed because: "+responseDescription;


            }
            ServerLog.log(NAME, "f~Swap failed cuz: "+"You didn't book "+oldEventID);
            return "f~Swap failed cuz: "+"You didn't book "+oldEventID;


        }

    }
}
