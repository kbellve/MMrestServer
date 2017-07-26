package edu.umassmed.big.mmremote.handlers;

import com.google.gson.Gson;
import org.micromanager.data.Coords;
import org.micromanager.data.Image;
import org.micromanager.display.DisplayWindow;
import org.micromanager.data.Datastore;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmKNIME;

import java.io.IOException;


/**
 *  Handle change requests.
 * 
 *  @author kdb
 */
public class SetDatastore extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
    	mmKNIME.core.logMessage("Creating a datastore");
        
        try {
        	boolean bMultiTiff = false, bSplit = false, bMetadata=false;
        	String sDirectory = "/tmp";
        	Datastore store = null;
        	DisplayWindow display = null;;
        	message         = new Message("OK");
            
        	if (params.containsKey("hash")) {
        		//find data store for hash
        		
        	}
        	else throw new MissingKeyException();
        	
            if (params.containsKey("ram")) {
            	store = mmKNIME.si.data().createRAMDatastore();
            	mmKNIME.core.logMessage("Creating a RAM datastore");
            }
            else if (params.containsKey("tiff")) {
            	bMultiTiff = true;
            	if (params.containsKey("directory")) sDirectory = params.get("directory").toString();
            	if (params.containsKey("metadata")) bMetadata = Boolean.parseBoolean(params.get("metadata").toString());
            	if (params.containsKey("split")) bSplit= Boolean.parseBoolean(params.get("split").toString());
            	store = mmKNIME.si.data().createMultipageTIFFDatastore(sDirectory, bMultiTiff, bSplit);
            	mmKNIME.core.logMessage("Creating a Multipage TIFF datastore");
            }
            
            if (store != null) display = mmKNIME.si.displays().createDisplay(store);
            else throw new org.micromanager.internal.utils.MMException("Failed to create Datastore");
            
        } catch (MissingKeyException e) {
            message         = new Message("ERROR");
            message.error   = "SET DATASTORE requests requires the field 'name'.";
        } catch (org.micromanager.internal.utils.MMException e) {
            message         = new Message("ERROR");
            message.error   = "SET DATASTORE failed";
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle SET DATASTORE request.";
            mmKNIME.si.getLogManager().showError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
    
    private class MissingKeyException extends Exception { }
}