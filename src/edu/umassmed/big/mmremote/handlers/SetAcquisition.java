package edu.umassmed.big.mmremote.handlers;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.µmKNIME;

import java.io.IOException;
import org.micromanager.utils.ReportingUtils;

/**
 *  Handle change requests.
 * 
 *  @author kdb
 */
public class SetAcquisition extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
        ReportingUtils.logMessage("Opening an Acquisition");
        
        try {
            message         = new Message("OK");
            if (!params.containsKey("name") || !params.containsKey("directory") || !params.containsKey("frames")
             || !params.containsKey("channels") || !params.containsKey("slices") || !params.containsKey("positions")
             || !params.containsKey("show") || !params.containsKey("save"))
                throw new MissingKeyException();
            
            String name        	= params.get("name").toString();
            String directory    = params.get("directory").toString();
            int frames    		= Integer.parseInt(params.get("frames").toString());
            int channels    	= Integer.parseInt(params.get("channels").toString());
            int slices    		= Integer.parseInt(params.get("slices").toString());
            int positions   	= Integer.parseInt(params.get("positions").toString());
            boolean show    	= Boolean.parseBoolean(params.get("show").toString());
            boolean save    	= Boolean.parseBoolean(params.get("save").toString()); 
            
            µmKNIME.si.openAcquisition(name,  directory, frames, channels, slices, positions, show, save);

            
        } catch (MissingKeyException e) {
            message         = new Message("ERROR");
            message.error   = "SET ACQUISITION requests requires the fields 'name', 'directory', 'frames','channels', 'slices', 'positions', 'show' and 'save' to be set.";
        } catch (org.micromanager.utils.MMScriptException e) {
            message         = new Message("ERROR");
            message.error   = "SET ACQUISITION failed";
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle SET ACQUISITION request.";
            ReportingUtils.logError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
    
    private class MissingKeyException extends Exception { }
}