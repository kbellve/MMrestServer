package edu.umassmed.big.restserver.handlers;

import java.io.InputStream;

/**
 *  Load the overview page.
 * 
 *  @author Matthijs
 */
public class IndexHandler extends PageHandler {

    
    @Override
    protected InputStream getFileStream () {
        // Read file from a resource folder inside the JAR:
        return IndexHandler.class.getResourceAsStream("../resources/overview.html");    
    }
    
}
