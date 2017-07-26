package edu.umassmed.big.mmremote.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.umassmed.big.mmremote.mmKNIME;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 *  Handle loading a page. In essence, this is an abstract wrapper around a file
 *  reader for a single HTML page. Extending classes should implement the
 *  getFileStream method, and optionally load additional data required for 
 *  displaying the file.
 * 
 *  @author Matthijs
 */
abstract public class PageHandler implements HttpHandler  {

    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
    	mmKNIME.core.logMessage("µmKNIME: Handling overview request.");        
        
        java.util.Scanner scanner   = new java.util.Scanner(getFileStream()).useDelimiter("\\A");
        String response             = scanner.hasNext() ? scanner.next() : "";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os     = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
    
    abstract protected InputStream getFileStream ();
}