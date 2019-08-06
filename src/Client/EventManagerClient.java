package Client;

import FrontEndApp.FrontEnd;
import FrontEndApp.FrontEndHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Scanner;

public class EventManagerClient {

   private Scanner sc= new Scanner(System.in);
   private ORB orb;
    private String managerID;
    private static final String[] locations = {"MTL", "TOR", "OTW"};



    public EventManagerClient(ORB orb) throws SecurityException{
        this.orb=orb;

    }

    FrontEnd frontend;
    public void showMenu() throws RemoteException {
        while(true) {

            System.out.println("Please choose your operation: ");
            System.out.println("1. Book event\n" +
                    "2. Show my booking schedule\n" +
                    "3. Cancel event booking\n" +
                    "4. Add event\n" +
                    "5. Remove event\n" +
                    "6. List event availability\n" +
                    "7. Swap event\n"+
                    "8. Exit");

            boolean validChoice = false;

            while (!validChoice) {
                String choice = sc.nextLine();
                switch (choice) {
                    case "1":
                        bookEvent();
                        validChoice = true;
                        break;
                    case "2":
                        getBookingSchedule();
                        validChoice = true;
                        break;
                    case "3":
                        cancelEvent();
                        validChoice = true;
                        break;
                    case "4":
                        addEvent();
                        validChoice = true;
                        break;
                    case "5":
                        removeEvent();
                        validChoice = true;
                        break;
                    case "6":
                        listEventAvailability();
                        validChoice = true;
                        break;
                    case "7":
                        swapEvent();
                        validChoice=true;
                        break;
                    case "8":
                        // quit
                        System.out.println("Bye-bye.");
                        System.exit(0);
                        break;
                    default:
                        break;
                }

            }


        }

    }
    public void swapEvent()throws RemoteException{



        String customerID= getCorrectCustomerID();
        System.out.println("--------------oldevent-------------");
        String oldEventType = getCorrectEventType();
        System.out.println("Please enter the id of the oldevent:");
        String oldEventID=sc.nextLine();
        System.out.println("--------------newevent-------------");
        String newEventType = getCorrectEventType();
        System.out.println("Please enter the id of the newevent:");
        String newEventID=sc.nextLine();

        swapEvent(customerID,newEventID,newEventType,oldEventID,oldEventType);





    }
    public void swapEvent(String customerID,String  newEventID, String newEventType, String oldEventID,String oldEventType)throws RemoteException{

        String request = customerID+"swap"+oldEventType+":"+oldEventID+"with"+newEventType+":"+newEventID;
        System.out.println("Send swap event request to server: "+request);
        ClientLog.log(managerID, "Send swap event request to server: "+request);

        String response=frontend.swapEvent(customerID,newEventID,newEventType,oldEventID,oldEventType);

        System.out.println(response);
        ClientLog.log(managerID, response);

    }

    public void addEvent() throws RemoteException{
        String eventType = getCorrectEventType();

        String eventID = "";
        int capacity = 0;

        System.out.print("Please enter event ID: ");
        eventID = sc.nextLine();

        //while(!validEvent){
        System.out.println("Please enter the capacity: ");
        try{
            capacity = Integer.parseInt(sc.nextLine());
            if(capacity <= 0){
                System.out.println("Invalid Capacity");
                return;
            }

        }
        catch (Exception e){
            System.out.println("Invalid Capacity");
            return;
        }
        //}
        //Event event = new Event(eventType, date, time, managerID.substring(0,3), capacity);
        //Event event = new Event(eventID, eventType, capacity);
        addEvent(eventID,eventType,capacity);

    }
    public void addEvent(String eventID, String eventType, int capacity) throws RemoteException{
        String request = eventID+"-"+eventID+"-"+capacity;

        System.out.println("Send add event request to server: "+request);
        ClientLog.log(managerID,"Send add event request to server: " + request);

        String response =
                frontend.addEvent(managerID,eventID, eventType, capacity);
        System.out.println(response);
        ClientLog.log(managerID,response);
    }

    public void removeEvent() throws RemoteException{
        String eventType = getCorrectEventType();

        System.out.println("Please enter the ID of the event that you want to remove: ");
        String eventID = sc.nextLine();

        removeEvent(eventID, eventType);
    }

    public void removeEvent(String eventID, String eventType) throws RemoteException{

        System.out.println("Send remove event request to server: "+eventID+"-"+eventType);
        ClientLog.log(managerID, "Send remove event request to server: "+eventID+"-"+eventType);

//        if(frontend.removeEvent(eventID, eventType, managerID).substring(0,7)=="Success"){
//            System.out.println("Successfully remove the event");
//            ClientLog.log(managerID, "Successfully remove the event");
//        }
//        else{
//            System.out.println(frontend.);
//            ClientLog.log(managerID, "Fail to remove the event. Please make sure you enter the correct event id");
//        }
        String response=frontend.removeEvent(managerID,eventID,eventType);
        System.out.println(response);
        ClientLog.log(managerID,response);
    }

    public void listEventAvailability() throws RemoteException{
        String eventType = getCorrectEventType();

        listEventAvailability(eventType);
    }

    public void listEventAvailability(String eventType) throws RemoteException{
        System.out.println("Send list event availability request to server: "+eventType);
        ClientLog.log(managerID, "Send list event availability request to server: "+eventType);

        String list = frontend.listEventAvailability(managerID, eventType);
        System.out.println(list);
        ClientLog.log(managerID, list);
    }

    public void bookEvent() throws RemoteException{

        String eventType = getCorrectEventType();
        String customerID = getCorrectCustomerID();

        System.out.println("Please enter the event ID: ");
        String eventID = sc.nextLine();
        bookEvent(customerID, eventID, eventType);
    }

    public void bookEvent(String customerID, String eventID, String eventType) throws RemoteException{

        String request = customerID+"-"+eventID+"-"+eventType;
        System.out.println("Send book event request to server: "+request);
        ClientLog.log(managerID,request);

        String response = frontend.bookEvent(customerID,eventID,eventType);
        System.out.println(response);

        ClientLog.log(managerID, response);
    }

    public void getBookingSchedule() throws RemoteException{
        String customerID = getCorrectCustomerID();
        getBookingSchedule(customerID);
    }

    public void getBookingSchedule(String customerID) throws RemoteException{
        System.out.println("Send get booking schedule request to server "+customerID);
        ClientLog.log(managerID, "Send get booking schedule request to server "+customerID);
        String list = frontend.getBookingSchedule(customerID);
        System.out.println(list);
        ClientLog.log(managerID, list);
    }

    public void cancelEvent() throws RemoteException{
        String eventType = getCorrectEventType();
        String customerID = getCorrectCustomerID();
        if(!customerID.substring(0,3).equals(managerID.substring(0,3))){
            System.out.println("You are not permitted to cancel event for customers from other cities");
            return;
        }
        System.out.println("Please enter the event ID: ");
        String eventID = sc.nextLine();
        cancelEvent(customerID, eventID, eventType);
    }

    public void cancelEvent(String customerID, String eventID, String eventType) throws RemoteException{
        System.out.println("Send cancel booking request to server: "+customerID+"-"+eventID+"-"+eventType);
        ClientLog.log(managerID, "Send cancel booking request to server: "+customerID+"-"+eventID+"-"+eventType);

        String response = frontend.cancelEvent(customerID,eventID,eventType);

        System.out.println(response);
        ClientLog.log(managerID, response);
    }

    public void login() throws Exception{
        System.out.println("-------------Event Manager------------");
        System.out.println("Please input your manager ID to login:");

        while(true){
            managerID = sc.nextLine();
            if(managerID.length() == 8) {
                String loc = managerID.substring(0, 3);
                String role = managerID.substring(3, 4);
                String id = managerID.substring(4);
                if(Arrays.asList(locations).contains(loc)){
                    if(role.equals("M")){
                        try{
                            Integer.parseInt(id);
                            break;
                        }
                        catch (Exception e){
                            // do nothing
                        }
                    }
                }
            }


            System.out.println("Invalid manager ID. Please check your input and try again.");
        }


        ConnectedtoFrontEnd();


        showMenu();
    }


    public void ConnectedtoFrontEnd()throws Exception{

        org.omg.CORBA.Object objRefM = orb.resolve_initial_references("NameService");
        NamingContextExt ncRefM = NamingContextExtHelper.narrow(objRefM);

        frontend = (FrontEnd) FrontEndHelper.narrow(ncRefM.resolve_str("FRONTEND"));
        ClientLog.createLog(managerID);
        ClientLog.log(managerID, "Connect to FrontEnd");
        System.out.println("Connect to FrontEnd");

    }
    public static String getCorrectEventType(){

        System.out.println("Please choose the event type: \n" +
                "1. Conferences\n2. Seminars\n3. Trade Shows");
        Scanner sc = new Scanner(System.in);
        String choice;
        while(true){
            choice = sc.nextLine();
            if(choice.equals("1") || choice.equals("2") || choice.equals("3")){
                break;
            }
            System.out.println("Invalid choice, please enter a number from 1 to 3");
        }

        String eventType;

        if(choice.equals("1")){
            eventType = "Conferences";
        }
        else if(choice.equals("2")){
            eventType = "Seminars";
        }
        else{
            eventType = "Trade Shows";
        }
        return eventType;
    }

    public  String getCorrectCustomerID(){
        System.out.println("Please enter the customer ID: ");
        boolean validID = false;
        String customerID;
        while(true){
            customerID = sc.nextLine();
            if(customerID.length() == 8) {
                String loc = customerID.substring(0, 3);
                String role = customerID.substring(3, 4);
                String id = customerID.substring(4);
                if(Arrays.asList(locations).contains(loc)){
                    if(role.equals("C")){
                        try{
                            Integer.parseInt(id);
                            break;
                        }
                        catch (Exception e){
                            // do nothing
                        }
                    }
                }
            }

            System.out.println("Invalid customer ID. Please check your input and try again.");
        }

        return customerID;
    }

    public static void main(String[] args) throws Exception {

        String[] params = {"-ORBInitialPort", "1050",  "-ORBInitialHost", "localhost"};
        ORB orb=ORB.init(params,null);

        System.out.println("orb has started");
        new EventManagerClient(orb).login();

    }

}
