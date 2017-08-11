package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;
import java.util.Iterator;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.display.DisplayWindow;
import com.google.gson.Gson;
import edu.umassmed.big.mmremote.mmKNIME;
import edu.umassmed.big.mmremote.CreateDatastore;
import edu.umassmed.big.mmremote.CreateCoordinates;
import edu.umassmed.big.mmremote.Message;
import mmcorej.TaggedImage;


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
            
            if (params.containsKey("title")) {
            	// lets try and find window matching title and use that as a display
            	Iterator ImageWindow = mmKNIME.si.getDisplayManager().getAllImageWindows().iterator();
            	
            	while (ImageWindow.hasNext()){
            		display = (DisplayWindow)ImageWindow.next();
            		System.out.println(display.getName());
            		if (params.get("title").toString().equals(display.getName())) {
            			break;            			
            		} else display = null;
            	}
            }
        	
        	// check if we have any coordinates first, if we have no coordinates, then just snap a live image
        	Coords coord = new CreateCoordinates().parseParams(params);
        	if (coord == null) {
        		mmKNIME.core.logMessage("µmKNIME: Snapping a live image since no coordinates were passed."); 
        		mmKNIME.si.live().snap(true);
        		display = mmKNIME.si.live().getDisplay();  	
        	}
        	else {
  	            mmKNIME.core.logMessage("µmKNIME: Snapping a single image.");         
	            mmKNIME.core.snapImage();              
	            if (display == null) 
	            {
	            	mmKNIME.core.logMessage("µmKNIME: Creating new datastore...");  
	            	store = new CreateDatastore().parseParams(params);
	                if (store != null) { 
	                	mmKNIME.core.logMessage("µmKNIME: Creating new window with attached datastore...");  
	                    display = mmKNIME.si.displays().createDisplay(store);
	                }
	            } else store = display.getDatastore();
	            	               
	            if (store != null) {    
	            	if (store.getImage(coord) != null) mmKNIME.core.logMessage("µmKNIME: Datastore does not allow overwrites at the same coordinates...");
	            	else {
	            		mmKNIME.core.logMessage("µmKNIME: Adding new image to datastore...");  
		                TaggedImage tmp = mmKNIME.core.getTaggedImage();
		                if (tmp != null) {
		                	mmKNIME.core.logMessage("µmKNIME: Adding new tagged image to datastore...");  
		                	Image image = mmKNIME.si.data().convertTaggedImage(tmp);
		                	image = image.copyAtCoords(coord);
		                	
		                	if (image != null)  store.putImage(image); 
		                }
	                }  	
	            }    
        	}
                
            if (params.containsKey("title")) {
            	if (display != null) {
            		mmKNIME.core.logMessage("µmKNIME: Changing title of window");  
            		display.setCustomTitle(params.get("title").toString());
            	}
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
