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
public class SetPosition extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
        ReportingUtils.logMessage("µmKNIME: Generating SET X, Y or Z position response.");
        
        Double dXpos = 0.0,dYpos = 0.0,dZpos = 0.0;
        
        try {
            message         = new Message("OK");
            if (!params.containsKey("x") && !params.containsKey("y") && !params.containsKey("z")
            		&& !params.containsKey("Home") && !params.containsKey("Origin"))
                throw new MissingKeyException();
           
            
            dXpos = µmKNIME.core.getXPosition();
            dYpos = µmKNIME.core.getYPosition();
            dZpos = µmKNIME.core.getPosition();
            
            if (params.containsKey("X")) {
            	dXpos  = Double.parseDouble(params.get("x").toString());
            	ReportingUtils.logMessage("µmKNIME: Setting X Postion to: " + dXpos);
            	µmKNIME.core.setXYPosition(dXpos, dYpos);
            }
            if (params.containsKey("Y")) {
            	dYpos  = Double.parseDouble(params.get("y").toString());
            	ReportingUtils.logMessage("µmKNIME: Setting Y Postion to: " + dYpos);
            	µmKNIME.core.setXYPosition(dXpos, dYpos);
            }
            if (params.containsKey("Z")) {
            	dZpos  = Double.parseDouble(params.get("z").toString());
            	ReportingUtils.logMessage("µmKNIME: Setting Z Postion to: " + dZpos);
            	µmKNIME.core.setPosition(dZpos);
            }
            
            if (params.containsKey("Home")) {
            	if (params.get("Home").toString() == "xy")
            		µmKNIME.core.home(µmKNIME.core.getXYStageDevice());
            	if (params.get("Home").toString() == "z")
            		µmKNIME.core.home(µmKNIME.core.getFocusDevice());
            }
            
            if (params.containsKey("Origin")) {
            	if (params.get("Origin").toString() == "xy")
            		µmKNIME.core.setOriginXY();
            	if (params.get("Origin").toString() == "z")
            		µmKNIME.core.setOrigin();
            }
        
            
        } catch (MissingKeyException e) {
            message         = new Message("ERROR");
            message.error   = "µmKNIME: SetPosition requests requires either the fields 'x', 'y', 'z','HOME' or 'ORIGIN' to be set.";
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "µmKNIME: Could not handle SET POSITION request.";
            ReportingUtils.logError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
    
    private class MissingKeyException extends Exception { }
}