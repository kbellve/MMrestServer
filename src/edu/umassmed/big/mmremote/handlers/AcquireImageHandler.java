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
public class AcquireImageHandler  extends Handler {
    
    
    @Override
    protected String getResponse() throws IOException {
        ReportingUtils.logMessage("µmKNIME: Generating Snap Image response.");
        
        try {
            message         = new Message("OK");
            ReportingUtils.logMessage("µmKNIME: Snapping a single image.");
            
            // Optionally change exposure before snapping image
            if (params.containsKey("exposure")) {
            	double exposure = Double.parseDouble(params.get("exposure").toString());
            	µmKNIME.core.setExposure(exposure);
            }
            
            µmKNIME.si.snapSingleImage();
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle Snap Image request.";
            ReportingUtils.logError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
}
