package edu.umassmed.big.mmremote.handlers;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmKNIME;

import java.io.IOException;

/**
 *  Handle change requests.
 * 
 *  @author kdb
 */
public class SetProperty extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
    	mmKNIME.core.logMessage("µmKNIME: Generating SET response.");
        
        try {
            message         = new Message("OK");
            if (!params.containsKey("device") || !params.containsKey("property") || !params.containsKey("value"))
                throw new MissingKeyException();
        
            String label        = params.get("device").toString();
            String propName     = params.get("property").toString();
            String propValue    = params.get("value").toString();
            
            mmKNIME.core.setProperty(label, propName, Integer.parseInt(propValue));
            
            
            
        } catch (MissingKeyException e) {
            message         = new Message("ERROR");
            message.error   = "SetProperty requests require the fields 'device', 'property' and 'value' to be set.";
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle SET request.";
            mmKNIME.si.getLogManager().showError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
    
    private class MissingKeyException extends Exception { }
}