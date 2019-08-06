package RM3;

import java.util.ArrayList;

public interface ServerInterface {

    public final static int MUDPPORT = 1200;
    public final static int OUDPPORT = 1201;
    public final static int TUDPPORT = 1202;

    public String addEvent(String MID, String eventID, String eventType, int bookingCapacity) throws Exception;

    public String removeEvent(String MID, String eventID, String eventType) throws Exception;

    public String listEventAvailability(String MID, String eventType) throws Exception;

    public String bookEvent (String customerID,String eventID, String eventType) throws Exception;

    public String getBookingSchedule(String customerID) throws Exception;

    public String cancelEvent(String customerID, String eventID, String eventType) throws Exception;

    public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) throws Exception;


}
