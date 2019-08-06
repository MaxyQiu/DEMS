package RM4;

import java.util.ArrayList;
import java.util.Scanner;

public class Event {

    private String eventID;
    private int capacity;
    private int bookedNumPeople;
    private String eventType;
    private String date;
    private String time;
    private String location;
    private ArrayList<String> bookedCustomerList = new ArrayList<>();

    // to do
    // can rm bookedNumPeople

    public Event(String eventType, String date, String time, String location, int capacity){
        this.eventType = eventType;
        this.date = date;
        this.time = time;
        this.location = location;
        this.capacity = capacity;

        this.eventID = location+time+date;


    }

    public Event(String eventID, String eventType, int capacity){
        this.eventID = eventID;
        this.eventType = eventType;
        this.capacity = capacity;
    }

    public boolean book(String customerID){
        synchronized (this){
            if(bookedNumPeople < capacity) {
                bookedNumPeople++;
                bookedCustomerList.add(customerID);
                return true;
            }
            return false;
        }
    }

    public boolean cancel(String customerID){
        synchronized (this){
            if(this.isBookedBy(customerID)){
                this.bookedCustomerList.remove(customerID);
                bookedNumPeople--;
                return true;
            }
            return false;
        }
    }

    public boolean isFull(){
       return (this.bookedNumPeople >= this.capacity);
    }

    public String getEventID() {
        return eventID;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getBookedNumPeople() {
        return bookedNumPeople;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    private void updateEventID() {
        this.eventID = this.location+this.time+this.date;

    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setBookedNumPeople(int bookedNumPeople) {
        this.bookedNumPeople = bookedNumPeople;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
        updateEventID();
    }

    public void setDate(String date) {
        this.date = date;
        updateEventID();
    }

    public void setTime(String time) {
        this.time = time;
        updateEventID();
    }

    public void setLocation(String location) {
        this.location = location;
        updateEventID();
    }

    public int getAvailability(){
        return this.capacity - this.bookedNumPeople;
    }

    public ArrayList<String> getBookedCustomerList(){
        return this.bookedCustomerList;
    }

    public boolean isBookedBy(String customerID){
        if(this.bookedCustomerList.size() == 0){
            return false;
        }

        for (String cID:this.bookedCustomerList) {
            if(cID.equals(customerID)){
                return true;
            }
        }
        return false;
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




}
