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
public class SetPosition extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
    	mmKNIME.core.logMessage("µmKNIME: Generating SET X, Y or Z position response.");
        
        Double dXpos = 0.0,dYpos = 0.0,dZpos = 0.0;
        
        try {
            message         = new Message("OK");
            if (!params.containsKey("x") && !params.containsKey("y") && !params.containsKey("z")
            		&& !params.containsKey("Home") && !params.containsKey("Origin"))
                throw new MissingKeyException();
           
            
            dXpos = mmKNIME.core.getXPosition();
            dYpos = mmKNIME.core.getYPosition();
            dZpos = mmKNIME.core.getPosition();
            
            if (params.containsKey("X")) {
            	dXpos  = Double.parseDouble(params.get("x").toString());
            	mmKNIME.core.logMessage("µmKNIME: Setting X Postion to: " + dXpos);
            	mmKNIME.core.setXYPosition(dXpos, dYpos);
            }
            if (params.containsKey("Y")) {
            	dYpos  = Double.parseDouble(params.get("y").toString());
            	mmKNIME.core.logMessage("µmKNIME: Setting Y Postion to: " + dYpos);
            	mmKNIME.core.setXYPosition(dXpos, dYpos);
            }
            if (params.containsKey("Z")) {
            	dZpos  = Double.parseDouble(params.get("z").toString());
            	mmKNIME.core.logMessage("µmKNIME: Setting Z Postion to: " + dZpos);
            	mmKNIME.core.setPosition(dZpos);
            }
            
            if (params.containsKey("Home")) {
            	if (params.get("Home").toString() == "xy")
            		mmKNIME.core.home(mmKNIME.core.getXYStageDevice());
            	if (params.get("Home").toString() == "z")
            		mmKNIME.core.home(mmKNIME.core.getFocusDevice());
            }
            
            if (params.containsKey("Origin")) {
            	if (params.get("Origin").toString() == "xy")
            		mmKNIME.core.setOriginXY();
            	if (params.get("Origin").toString() == "z")
            		mmKNIME.core.setOrigin();
            }
        
            
        } catch (MissingKeyException e) {
            message         = new Message("ERROR");
            message.error   = "SetPosition requests requires either the fields 'x', 'y', 'z','HOME' or 'ORIGIN' to be set.";
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle SET POSITION request.";
            mmKNIME.si.getLogManager().showError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
    
    private class MissingKeyException extends Exception { }
}