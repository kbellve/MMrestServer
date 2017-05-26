package edu.umassmed.big.restserver;

import com.sun.net.httpserver.HttpServer;

import edu.umassmed.big.restserver.handlers.ImageGetHandler;
import edu.umassmed.big.restserver.handlers.ImageSingleSnapHandler;
import edu.umassmed.big.restserver.handlers.ImageSnapandAddHandler;
import edu.umassmed.big.restserver.handlers.ImageViewHandler;
import edu.umassmed.big.restserver.handlers.IndexHandler;
import edu.umassmed.big.restserver.handlers.SetAcquisition;
import edu.umassmed.big.restserver.handlers.SetPosition;
import edu.umassmed.big.restserver.handlers.SetProperty;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *  HTTP service. When started, this will listen on the selected port for 
 *  incoming connections and HTTP requests.
 * 
 *  @author Matthijs
 *  @author kdb
 */
public class Service {

    private static Service instance     =   null;
    private HttpServer     server       =   null;
    
    
    /**
     *  Start the HTTP service. If no server context exists, create one.
     */
    public static void start () throws Exception {
        if (instance == null)
            instance = new Service();
        instance.server.start();
    }
    
    /**
     *  Stop the HTTP service, if it exists.
     */
    public static void stop () {
        if (instance != null)
            instance.server.stop(0);
    }
    
    
    /**
     *  Create a new HTTP server instance. Register default handlers for the
     *  various contexts.
     * 
     * @throws Exception 
     */
    private Service() throws Exception {
        createServer();

        // Internal documentation handlers:
        (server.createContext("/snap/image/",    	new ImageSingleSnapHandler())).getFilters().add(new ParameterFilter());
        (server.createContext("/snap/imageandadd/", new ImageSnapandAddHandler())).getFilters().add(new ParameterFilter());
        server.createContext("/view/image/",    	new ImageViewHandler());
        server.createContext("/",               	new IndexHandler());
        
        // SET / GET request handlers:
        (server.createContext("/get/image/",    	new ImageGetHandler())).getFilters().add(new ParameterFilter());
        (server.createContext("/set/property/", 	new SetProperty())).getFilters().add(new ParameterFilter());
        (server.createContext("/set/position/", 	new SetPosition())).getFilters().add(new ParameterFilter());
        (server.createContext("/set/acquisition/", 	new SetAcquisition())).getFilters().add(new ParameterFilter());
    }
    
    
    /**
     *  Attempt to create a server and set a default executer.
     * @throws IOException 
     */
    private void createServer () throws IOException {
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.setExecutor(null); // creates a default executor        
    }
}