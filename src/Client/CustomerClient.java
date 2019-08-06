package Client;
import java.util.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

import FrontEndApp.FrontEnd;
import FrontEndApp.FrontEndHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
//import
public class CustomerClient {

    private Scanner sc= new Scanner(System.in);
    public String customerID;
    private static final String[] locations = {"MTL", "TOR", "OTW"};
    private ORB orb;

    public CustomerClient(ORB orb) throws SecurityException {
        this.orb = orb;

    }
    FrontEnd frontend;



    public void showMenu() throws RemoteException{

        while(true) {

            System.out.println("Please choose your operation: ");
            System.out.println("1. Book event\n" +
                    "2. Show my booking schedule\n" +
                    "3. Cancel event booking\n" +"4. Swap the event\n"+
                    "5. Exit");

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
                    case"4":
                        swapEvent();
                        validChoice=true;
                        break;
                    case "5":
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

        String request = customerID+"swap "+oldEventType+":"+oldEventID+" with "+newEventType+":"+newEventID;
        System.out.println("Send swap event request to server: "+request);
        ClientLog.log(customerID, "Send swap event request to server: "+request);

        String response=frontend.swapEvent(customerID,newEventID,newEventType,oldEventID,oldEventType);

        System.out.println(response);
        ClientLog.log(customerID, response);

    }


    public void bookEvent() throws RemoteException{

        String eventType = getCorrectEventType();

        System.out.println("Please enter the event ID: ");

        String eventID = sc.nextLine();

        bookEvent(customerID, eventID, eventType);
    }

    public void bookEvent(String customerID, String eventID, String eventType) throws RemoteException{

        String request = customerID+"-"+eventID+"-"+eventType;
        System.out.println("Send book event request to server: "+request);
        ClientLog.log(customerID,"Send book event request to server: "+request);

        String response = frontend.bookEvent(customerID,eventID,eventType);
        System.out.println(response);
        ClientLog.log(customerID, response);
    }

    public void getBookingSchedule() throws RemoteException{
        getBookingSchedule(customerID);
    }

    public void getBookingSchedule(String customerID) throws RemoteException{
        System.out.println("Send get booking schedule request to server "+customerID);
        ClientLog.log(customerID, "Send get booking schedule request to server "+customerID);
        String list = frontend.getBookingSchedule(customerID);
        System.out.println(list);
        ClientLog.log(customerID, list);
    }

    public void cancelEvent() throws RemoteException{
        String eventType = getCorrectEventType();
        System.out.println("Please enter the event ID: ");
        String eventID = sc.nextLine();
        cancelEvent(customerID, eventID, eventType);
    }

    public void cancelEvent(String customerID, String eventID, String eventType) throws RemoteException{

        System.out.println("Send cancel booking request to server: "+customerID+"-"+eventID+"-"+eventType);
        ClientLog.log(customerID, "Send cancel booking request to server: "+customerID+"-"+eventID+"-"+eventType);

        String response = frontend.cancelEvent(customerID,eventID,eventType);

        System.out.println(response);
        ClientLog.log(customerID, response);
    }

    public void login() throws Exception {

        System.out.println("-------------Customer------------");
        System.out.println("Please input your customer ID to login:");

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

        ConnectedtoFrontEnd();

        showMenu();

    }

    public   void ConnectedtoFrontEnd()throws Exception{

        org.omg.CORBA.Object objRefM = orb.resolve_initial_references("NameService");
        NamingContextExt ncRefM = NamingContextExtHelper.narrow(objRefM);

        frontend = (FrontEnd) FrontEndHelper.narrow(ncRefM.resolve_str("FRONTEND"));
        ClientLog.createLog(customerID);
        ClientLog.log(customerID, "Connect to FrontEnd");
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


    public static void main(String[] args) throws Exception{
        String[] params = {"-ORBInitialPort", "1050",  "-ORBInitialHost", "localhost"};
        ORB orb=ORB.init(params,null);

        System.out.println("orb has started");
        new CustomerClient(orb).login();

    }





}