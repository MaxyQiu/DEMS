package FrontEndApp;


/**
* FrontEndApp/_FrontEndStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/xwang/Documents/GitHub/COMP6231Project/src/FE/FrontEnd.idl
* Sunday, July 28, 2019 4:26:07 o'clock PM EDT
*/

public class _FrontEndStub extends org.omg.CORBA.portable.ObjectImpl implements FrontEndApp.FrontEnd
{

  public String addEvent (String managerID, String eventID, String eventType, int bookingCapacity)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("addEvent", true);
                $out.write_string (managerID);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $out.write_long (bookingCapacity);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return addEvent (managerID, eventID, eventType, bookingCapacity        );
            } finally {
                _releaseReply ($in);
            }
  } // addEvent

  public String removeEvent (String managerID, String eventID, String eventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("removeEvent", true);
                $out.write_string (managerID);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return removeEvent (managerID, eventID, eventType        );
            } finally {
                _releaseReply ($in);
            }
  } // removeEvent

  public String listEventAvailability (String managerID, String eventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("listEventAvailability", true);
                $out.write_string (managerID);
                $out.write_string (eventType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return listEventAvailability (managerID, eventType        );
            } finally {
                _releaseReply ($in);
            }
  } // listEventAvailability

  public String bookEvent (String customerID, String eventID, String eventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("bookEvent", true);
                $out.write_string (customerID);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return bookEvent (customerID, eventID, eventType        );
            } finally {
                _releaseReply ($in);
            }
  } // bookEvent

  public String getBookingSchedule (String customerID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getBookingSchedule", true);
                $out.write_string (customerID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getBookingSchedule (customerID        );
            } finally {
                _releaseReply ($in);
            }
  } // getBookingSchedule

  public String cancelEvent (String customerID, String eventID, String eventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("cancelEvent", true);
                $out.write_string (customerID);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return cancelEvent (customerID, eventID, eventType        );
            } finally {
                _releaseReply ($in);
            }
  } // cancelEvent

  public String swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("swapEvent", true);
                $out.write_string (customerID);
                $out.write_string (newEventID);
                $out.write_string (newEventType);
                $out.write_string (oldEventID);
                $out.write_string (oldEventType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return swapEvent (customerID, newEventID, newEventType, oldEventID, oldEventType        );
            } finally {
                _releaseReply ($in);
            }
  } // swapEvent

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:FrontEndApp/FrontEnd:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _FrontEndStub
