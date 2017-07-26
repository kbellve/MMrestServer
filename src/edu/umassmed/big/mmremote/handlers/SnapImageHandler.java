package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmKNIME;

/**
 *  
 * @author kdb
 */
public class SnapImageHandler  extends Handler {
    
    
    @Override
    protected String getResponse() throws IOException {
    	mmKNIME.core.logMessage("µmKNIME: Generating Snap Image response.");
        
        try {
            message         = new Message("OK");
            mmKNIME.core.logMessage("µmKNIME: Snapping a single image.");
            
            // Optionally change exposure before snapping image
            if (params.containsKey("exposure")) {
            	double exposure = Double.parseDouble(params.get("exposure").toString());
            	mmKNIME.core.setExposure(exposure);
            }
            
            mmKNIME.core.snapImage();
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle Snap Image request.";
            mmKNIME.si.getLogManager().showError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
}
