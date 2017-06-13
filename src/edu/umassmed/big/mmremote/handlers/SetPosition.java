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
public class SetPosition extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
        ReportingUtils.logMessage("Generating SET X, Y or Z position response.");
        
        Double dXpos = 0.0,dYpos = 0.0,dZpos = 0.0;
        
        try {
            message         = new Message("OK");
            if (!params.containsKey("X") && !params.containsKey("Y") && !params.containsKey("Z")
            		&& !params.containsKey("Home") && !params.containsKey("Origin"))
                throw new MissingKeyException();
            
            dXpos = RestServer.core.getXPosition();
            dYpos = RestServer.core.getYPosition();
            dZpos = RestServer.core.getPosition();
            
            if (params.containsKey("X")) {
            	dXpos  = Double.parseDouble(params.get("X").toString());
            	ReportingUtils.logMessage("Setting X Postion to: " + dXpos);
            	RestServer.core.setXYPosition(dXpos, dYpos);
            }
            if (params.containsKey("Y")) {
            	dYpos  = Double.parseDouble(params.get("Y").toString());
            	ReportingUtils.logMessage("Setting Y Postion to: " + dYpos);
            	RestServer.core.setXYPosition(dXpos, dYpos);
            }
            if (params.containsKey("Z")) {
            	dZpos  = Double.parseDouble(params.get("Z").toString());
            	ReportingUtils.logMessage("Setting Z Postion to: " + dZpos);
            	RestServer.core.setPosition(dZpos);
            }
            
            if (params.containsKey("Home")) {
            	if (params.get("Home").toString() == "XY")
            		RestServer.core.home(RestServer.core.getXYStageDevice());
            	if (params.get("Home").toString() == "Z")
            		RestServer.core.home(RestServer.core.getFocusDevice());
            }
            
            if (params.containsKey("Origin")) {
            	if (params.get("Origin").toString() == "XY")
            		RestServer.core.setOriginXY();
            	if (params.get("Origin").toString() == "Z")
            		RestServer.core.setOrigin();
            }
        
            
        } catch (MissingKeyException e) {
            message         = new Message("ERROR");
            message.error   = "SetPosition requests requires either the fields 'X', 'Y', 'Z','HOME' or 'ORIGIN' to be set.";
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle SET POSITION request.";
            ReportingUtils.logError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
    
    private class MissingKeyException extends Exception { }
}