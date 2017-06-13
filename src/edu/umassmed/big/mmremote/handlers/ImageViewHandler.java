package edu.umassmed.big.mmremote.handlers;

import java.io.InputStream;

/**
 *  
 * @author Matthijs
 */
public class ImageViewHandler  extends PageHandler {
    
    
    @Override
    protected InputStream getFileStream () {
        // Read file from a resource folder inside the JAR:
        return IndexHandler.class.getResourceAsStream("../resources/view_image.html");    
    }    
    
}
