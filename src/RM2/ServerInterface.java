package RM2;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public interface ServerInterface {
	public final int UDP_PORT_MTL = 4000;
    public final int UDP_PORT_OTW = 4001;
    public final int UDP_PORT_TOR = 4002;

    public ArrayList<String> addEvent(String MID, String eventID, String eventType, int bookingCapacity) throws Exception;

    public ArrayList<String> removeEvent(String MID, String eventID, String eventType) throws Exception;

    public ArrayList<String> listEventAvailability(String MID, String eventType) throws Exception;

    public ArrayList<String> bookEvent (String customerID,String eventID, String eventType) throws Exception;

    public ArrayList<String> getBookingSchedule(String customerID) throws Exception;

    public String cancelEvent(String customerID, String eventID, String eventType) throws Exception;

    public ArrayList<String> swapEvent(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) throws Exception;

    public ConcurrentHashMap<String, ArrayList<Integer>> listEventAvailabilityForUDP(String eventType) throws Exception;

    public ArrayList<String> bookEventForUDP(String customerID,String eventID,String eventType) throws Exception;

    public ArrayList<String> getBookingScheduleForUDP(String customerID) throws Exception;

    public String cancelEventForUDP(String customerID, String eventID, String eventType) throws Exception;
}
