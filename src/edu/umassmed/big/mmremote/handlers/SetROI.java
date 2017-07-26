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
public class SetROI extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
    	mmKNIME.core.logMessage("µmKNIME: Creating an ROI.");
        
        
        try {
        	int x,y,xSize,ySize;
        	
            message         = new Message("OK");
            if (!params.containsKey("x") && !params.containsKey("y") && !params.containsKey("xSize")
            		&& !params.containsKey("ySize"))
                throw new MissingKeyException();
           
            x  = Integer.parseInt(params.get("x").toString());

            y  = Integer.parseInt(params.get("y").toString());
            
            xSize  = Integer.parseInt(params.get("xSize").toString());
            
            ySize = Integer.parseInt(params.get("ySize").toString());
            
            mmKNIME.core.logMessage("µmKNIME: Setting ROI to (" + x +","+ y +","+ xSize +","+ ySize +")");
            
            mmKNIME.core.setROI(x, y, xSize, ySize);
            
            
                
            
        } catch (MissingKeyException e) {
            message         = new Message("ERROR");
            message.error   = "SetROI requests requires the fields 'x', 'y', 'xSize',and 'ySize'";
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle SET ROI request.";
            mmKNIME.si.getLogManager().showError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
    
    private class MissingKeyException extends Exception { }
}