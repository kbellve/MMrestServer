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
public class GetProperty extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
        ReportingUtils.logMessage("Generating GET response.");
        
        try {
            message         = new Message("OK");
            if (!params.containsKey("label") || !params.containsKey("propName"))
                throw new MissingKeyException();
        
            String label        = params.get("label").toString();
            String propName     = params.get("propName").toString();
           
            
            message = new Message(RestServer.core.getProperty(label, propName));
            
        } catch (MissingKeyException e) {
            message         = new Message("ERROR");
            message.error   = "GetProperty requests require the fields 'label', and 'propName to be set.";
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