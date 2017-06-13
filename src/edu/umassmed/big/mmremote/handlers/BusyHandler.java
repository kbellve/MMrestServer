package edu.umassmed.big.mmremote.handlers;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.RestServer;

import java.io.IOException;
import org.micromanager.utils.ReportingUtils;

/**
 *  Handle change requests.
 * 
 *  @author kdb
 */
public class BusyHandler extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
        ReportingUtils.logMessage("Opening an Acquisition");
        
        // This function checks to see if a particular device is busy if passed with a device=, using deviceBusy()
        // if nothing is passed, then the systemBusy() is called which will return true if any device is busy
        
        try {
            message         = new Message("FALSE");
            
            // These MM functions seem to be blocking, but it might be device dependent if it is blocking or not...
            if (params.containsKey("device")) {
            	String name        	= params.get("device").toString();
            	ReportingUtils.logMessage("Checking if " + name + " is busy");
            	if (RestServer.core.deviceBusy(name) == true) message = new Message ("TRUE");
            	else message = new Message ("FALSE");
            }
            else {
            	ReportingUtils.logMessage("Checking if µManager is busy");
            	if (RestServer.core.systemBusy() == true) message = new Message ("TRUE");
            	else message = new Message ("FALSE");
            }
            
                
        } catch (org.micromanager.utils.MMScriptException e) {
            message         = new Message("ERROR");
            message.error   = "GET BUSY failed";
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle GET BUSY request.";
            ReportingUtils.logError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
}