package edu.umassmed.big.mmremote.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.µmKNIME;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.micromanager.utils.ReportingUtils;

/**
 *  Handle GET (retrieval) requests. These should never modify anything, only
 *  provide information. This abstract base class should be extended by 
 *  classes that provide retrieval functionality.
 * 
 *  @author Matthijs
 */
abstract public class Handler implements HttpHandler  {

    protected Map<String, Object>   params;     // URI request parameters.
    protected Message               message;    // The message to send back.
    
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ReportingUtils.logMessage("Handling request.");        
        params = (Map<String, Object>) exchange.getAttribute("parameters");
        String response = getResponse();
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
    abstract protected String getResponse () throws IOException;
}