package edu.umassmed.big.mmremote;

import com.google.gson.Gson;
//import com.google.gson.internal.LinkedTreeMap;

import edu.umassmed.big.mmremote.handlers.Handler;

//import org.micromanager.data.Coords;
//import org.micromanager.data.Image;
//import org.micromanager.display.DisplayWindow;
import org.micromanager.data.Datastore;

import java.io.IOException;
import java.util.Map;


/**
 *  Handle change requests.
 * 
 *  @author kdb
 */
public class Datastore {
 
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
}