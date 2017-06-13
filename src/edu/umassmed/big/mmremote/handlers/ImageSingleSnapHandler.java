package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import org.micromanager.utils.ReportingUtils;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.RestServer;

/**
 *  
 * @author kdb
 */
public class ImageSingleSnapHandler  extends Handler {
    
    
    @Override
    protected String getResponse() throws IOException {
        ReportingUtils.logMessage("Generating Snap Image response.");
        
        try {
            message         = new Message("OK");
            ReportingUtils.logMessage("Snapping a single image.");
            
            // Optionally change exposure before snapping image
            if (!params.containsKey("exposure")) {
            	double exposure = Double.parseDouble(params.get("exposure").toString());
            	RestServer.core.setExposure(exposure);
            }
            
            RestServer.si.snapSingleImage();
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
