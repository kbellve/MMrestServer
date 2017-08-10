package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;


import com.google.gson.Gson;

import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.Coords;
import org.micromanager.display.DisplayWindow;

import mmcorej.TaggedImage;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.Coordinates;
import edu.umassmed.big.mmremote.Datastore;
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
        	Datastore store = null;
        	DisplayWindow display = null;
        	message         = new Message("OK");
              	
        	// Optionally change exposure before snapping image
            if (params.containsKey("exposure")) {
            	double exposure = Double.parseDouble(params.get("exposure").toString());
            	mmKNIME.core.setExposure(exposure);
            }
        	
        	// check if we have any coordinates first, if we have no coordinates, then just snap a live image
        	Coords coord = new Coordinates().parseParams(params);
        	if (coord == null) {
        		mmKNIME.core.logMessage("µmKNIME: Snapping a live image."); 
        		mmKNIME.si.live().snap(true);
        	}
        	else {
  	            mmKNIME.core.logMessage("µmKNIME: Snapping a single image.");         
	            mmKNIME.core.snapImage();
	                
	            mmKNIME.core.logMessage("µmKNIME: Checking for current window...");  
	            display = mmKNIME.si.displays().getCurrentWindow();
	            if (display == null) 
	            {
	            	mmKNIME.core.logMessage("µmKNIME: Creating new datastore...");  
	            	store = new Datastore().parseParams(params);
	                if (store != null) { 
	                	mmKNIME.core.logMessage("µmKNIME: Creating new window with attached datastore...");  
	                    display = mmKNIME.si.displays().createDisplay(store);
	                }
	            } else store = mmKNIME.si.displays().getCurrentWindow().getDatastore();
	            	               
	            if (store != null) {    
	            	mmKNIME.core.logMessage("µmKNIME: Adding new image to datastore...");  
	                TaggedImage tmp = mmKNIME.core.getTaggedImage();
	                if (tmp != null) {
	                	mmKNIME.core.logMessage("µmKNIME: Adding new tagged image to datastore...");  
	                	Image image = mmKNIME.si.data().convertTaggedImage(tmp);
	                	image = image.copyAtCoords(coord);
	                	if (image != null) store.putImage(image);        
	                }  	
	            }    
        	}
                
            if (params.containsKey("title")) {
            	if (display != null) display.setCustomTitle(params.get("title").toString());;
            }
           
            
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
