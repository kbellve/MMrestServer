package edu.umassmed.big.mmremote.handlers;

import com.google.gson.Gson;
//import com.google.gson.internal.LinkedTreeMap;

//import org.micromanager.data.Coords;
//import org.micromanager.data.Image;
//import org.micromanager.display.DisplayWindow;
import org.micromanager.data.Datastore;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmKNIME;

import java.io.IOException;
import java.util.Map;


/**
 *  Handle change requests.
 * 
 *  @author kdb
 */
public class SetDatastore extends Handler {

    
    @Override
    protected String getResponse() throws IOException {
    	mmKNIME.core.logMessage("µmKNIME: Creating a datastore");
        
        try {
        	Datastore store = parseParams(params);
        	
            if (store != null) {
            	message.payload.put("hash", Integer.toString(store.hashCode()));
            }
            
       // } catch (MissingKeyException e) {
        //    message         = new Message("ERROR");
       //     message.error   = "SET DATASTORE requests requires the field 'name'.";
        //} catch (org.micromanager.internal.utils.MMException e) {
        //    message         = new Message("ERROR");
        //    message.error   = "SET DATASTORE failed";
        } catch (Exception e) {
            message         = new Message("ERROR");
            message.error   = "Could not handle SET DATASTORE request.";
            mmKNIME.si.getLogManager().showError(e);
        }
        Gson gson           = new Gson();
        String response     = gson.toJson(message);
        return response;        
    }
 
    public Datastore parseParams (Map<String, Object>   params) throws IOException {
    	
    	Datastore store = null;
    	
    	try {
    	
	    	boolean bSplit = false, bMetadata = false,bManage = false;
	    	String sDirectory = "/tmp";
	    		              	
	    	if (params.containsKey("metadata")) bMetadata = Boolean.parseBoolean(params.get("metadata").toString());
	    	if (params.containsKey("split")) 	bSplit= Boolean.parseBoolean(params.get("split").toString());
	    	if (params.containsKey("manage")) 	bManage= Boolean.parseBoolean(params.get("manage").toString());
	    	
	        if (params.containsKey("multitiff")) {
	        	if (params.containsKey("directory")) sDirectory = params.get("directory").toString();
	        	store = mmKNIME.si.data().createMultipageTIFFDatastore(sDirectory, bMetadata, bSplit);
	        	mmKNIME.core.logMessage("µmKNIME: Created a Multipage TIFF datastore");
	        } else if (params.containsKey("singletiff")) {
	        	if (params.containsKey("directory")) sDirectory = params.get("directory").toString();
	        	store = mmKNIME.si.data().createSinglePlaneTIFFSeriesDatastore(sDirectory);
	        	mmKNIME.core.logMessage("µmKNIME: Created a Single Plane TIFF datastore");
	        
	        } else { //default data store
	        	store = mmKNIME.si.data().createRAMDatastore();
	            mmKNIME.core.logMessage("µmKNIME: Created a RAM datastore");
	        }
	        
	        if (store != null) {    
	        	if (bManage == true) {
	        		//DisplayWindow display;
	        		//display = mmKNIME.si.displays().createDisplay(store);  
	        		mmKNIME.si.displays().manage(store);
	        	}	        	
	        }
	        else throw new org.micromanager.internal.utils.MMException("Failed to create Datastore");
    	
    	}
        catch (Exception e) {
            mmKNIME.si.getLogManager().showError(e);        
        }
    	
    	return store;
    	
    	
    }
    private class MissingKeyException extends Exception { }
}