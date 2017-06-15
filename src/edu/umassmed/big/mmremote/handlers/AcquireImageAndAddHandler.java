package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import org.micromanager.utils.ReportingUtils;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.µmKNIME;


/**
 *  
 * @author kdb
 */
public class AcquireImageAndAddHandler  extends Handler {
    
    
    @Override
    protected String getResponse() throws IOException {
        ReportingUtils.logMessage("Generating Acquire and Add Image response.");
        
        try {
            message         = new Message("OK");
            
            if (!params.containsKey("name") || !params.containsKey("frame") || !params.containsKey("channel") 
            		|| !params.containsKey("slice") || !params.containsKey("position"))
                throw new MissingKeyException();
            
            if (params.containsKey("exposure")) {
            	double exposure = Double.parseDouble(params.get("exposure").toString());
            	µmKNIME.core.setExposure(exposure);
            }
        
            String name   	= params.get("name").toString();
            int frame    	= Integer.parseInt(params.get("frame").toString());
            int channel     = Integer.parseInt(params.get("channel").toString());
            int slice    	= Integer.parseInt(params.get("slice").toString());
            int position    = Integer.parseInt(params.get("position").toString());
            
            ReportingUtils.logMessage("Snapping a single image, with optional exposure, and adding it to an acquisition in the correct position.");
            µmKNIME.si.snapAndAddImage(name, frame, channel, slice, position);

        } catch (MissingKeyException e) {
            message         = new Message("ERROR");
            message.error   = "type something";
        } catch (org.micromanager.utils.MMScriptException e) {
            message         = new Message("ERROR");
            message.error   = "SnapandAddImage failed";
        }catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle Snap and Add request.";
            ReportingUtils.logError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 

    private class MissingKeyException extends Exception { }
}
