package edu.umassmed.big.mmremote;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import edu.umassmed.big.mmremote.handlers.BusyHandler;
//import edu.umassmed.big.mmremote.handlers.ImageGetHandler;
import edu.umassmed.big.mmremote.handlers.GetImage;
import edu.umassmed.big.mmremote.handlers.GetProperty;
import edu.umassmed.big.mmremote.handlers.IndexHandler;
import edu.umassmed.big.mmremote.handlers.SetAcquisition;
import edu.umassmed.big.mmremote.handlers.SetPosition;
import edu.umassmed.big.mmremote.handlers.SetProperty;
import edu.umassmed.big.mmremote.handlers.SetROI;
import edu.umassmed.big.mmremote.handlers.SnapImageHandler;

/**
 * HTTP service. When started, this will listen on the selected port for
 * incoming connections and HTTP requests.
 *
 * @author Matthijs
 * @author kdb
 */
public class Service {

	private static Service instance = null;

	/**
	 * Start the HTTP service. If no server context exists, create one.
	 */
	public static void start() throws Exception {
		if (Service.instance == null) {
			Service.instance = new Service();
		}
		Service.instance.server.start();
	}

	/**
	 * Stop the HTTP service, if it exists.
	 */
	public static void stop() {
		if (Service.instance != null) {
			Service.instance.server.stop(0);
		}
	}

	private HttpServer server = null;

	/**
	 * Create a new HTTP server instance. Register default handlers for the
	 * various contexts.
	 * 
	 * @throws Exception
	 */
	private Service() throws Exception {
		this.createServer();

		// Internal documentation handlers:
		// server.createContext("/view/image/", new ImageViewHandler());
		this.server.createContext("/", new IndexHandler());

		// POST -> Tells µManager to acquire an image
		(this.server.createContext("/snap/image/", new SnapImageHandler())).getFilters().add(new ParameterFilter());

		// SET / GET request handlers:
		(this.server.createContext("/get/busy/", new BusyHandler())).getFilters().add(new ParameterFilter());
		(this.server.createContext("/get/image/", new GetImage())).getFilters().add(new ParameterFilter());
		(this.server.createContext("/get/property/", new GetProperty())).getFilters().add(new ParameterFilter());

		(this.server.createContext("/set/acquisition/", new SetAcquisition())).getFilters().add(new ParameterFilter());
		(this.server.createContext("/set/property/", new SetProperty())).getFilters().add(new ParameterFilter());
		(this.server.createContext("/set/position/", new SetPosition())).getFilters().add(new ParameterFilter());
		(this.server.createContext("/set/ROI/", new SetROI())).getFilters().add(new ParameterFilter());
	}

	/**
	 * Attempt to create a server and set a default executer.
	 * 
	 * @throws IOException
	 */
	private void createServer() throws IOException {
		this.server = HttpServer.create(new InetSocketAddress(8000), 0);
		this.server.setExecutor(null); // creates a default executor
	}
}