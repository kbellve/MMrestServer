package edu.umassmed.big.mmremote.handlers;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.µmKNIME;
import mmcorej.StrVector;

import java.io.IOException;

import org.micromanager.utils.ReportingUtils;

/**
 *  Handle change requests.
 * 
 *  @author kdb
 */
public class GetProperty extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
        ReportingUtils.logMessage("µmKNIME: Generating GET response.");
        
        try {
            message         		= new Message("OK");
            LinkedTreeMap   map     = new LinkedTreeMap();            
            String      	value;
            
            // return all properties
            if (params.containsKey("device") && !params.containsKey("property")) 
            {	
            	String device        	= params.get("device").toString();
	            
	            StrVector   properties  = µmKNIME.core.getDevicePropertyNames(device);
	            
	            for (String property : properties) {
	                value = µmKNIME.core.getProperty(device, property);
	                map.put(property, value);
	            }
	            message.payload.put(device, map);
	        }
            else {	
	            // or just return a specific property
	            if (!params.containsKey("device") || !params.containsKey("property"))
	                throw new MissingKeyException();
	        
	            else {
		            String device		= params.get("device").toString();
		            String property    	= params.get("property").toString();
		             
		            value = µmKNIME.core.getProperty(device, property);
		            map.put(property, value);
		            
		            message.payload.put(device, map);
	            }
            }
            
            
        } catch (MissingKeyException e) {
            message         = new Message("ERROR");
            message.error   = "GetProperty requests require the fields 'device', and 'property to be set.";
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle GET request.";
            ReportingUtils.logError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
    
    private class MissingKeyException extends Exception { }
}