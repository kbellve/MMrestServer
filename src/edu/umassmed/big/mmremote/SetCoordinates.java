package edu.umassmed.big.mmremote;

import org.micromanager.data.Coords;

import java.io.IOException;
import java.util.Map;


/**
 *  Handle change requests.
 * 
 *  @author kdb
 */
public class SetCoordinates {

    
    public Coords parseParams (Map<String, Object>   params) throws IOException {
    	
    	Coords coords = null;
    	
    	try {
    	
	    	int channel = 0, position = 0, time = 0, z =0;
	    		              	
	    	if (params.containsKey("z") || params.containsKey("time") || params.containsKey("channel") || params.containsKey("position"))
	    	{		
		    	if (params.containsKey("z")) 		z 		 = Integer.parseInt(params.get("z").toString());
		    	if (params.containsKey("time")) 	time 	 = Integer.parseInt(params.get("time").toString());	    	
		    	if (params.containsKey("channel")) 	channel  = Integer.parseInt(params.get("channel").toString());
		    	if (params.containsKey("position")) position = Integer.parseInt(params.get("position").toString());
		    		    	
		    	Coords.CoordsBuilder builder = mmKNIME.si.data().getCoordsBuilder();
		    	builder = builder.z(z).time(time).channel(channel).stagePosition(position);
		    	coords = builder.build();
	    	}
    	
    	}
        catch (Exception e) {
            mmKNIME.si.getLogManager().showError(e);        
        }
    	
    	return coords;
    	
    	
    }
}