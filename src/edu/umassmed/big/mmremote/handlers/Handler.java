package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmKNIME;

/**
 * Handle GET (retrieval) requests. These should never modify anything, only
 * provide information. This abstract base class should be extended by classes
 * that provide retrieval functionality.
 *
 * @author Matthijs
 */
abstract public class Handler implements HttpHandler {

	protected Message message; // The message to send back.
	protected Map<String, Object> params; // URI request parameters.

	abstract protected String getResponse() throws IOException;

	@Override
	public void handle(final HttpExchange exchange) throws IOException {
		mmKNIME.core.logMessage("µmKNIME: Handling request.");
		this.params = (Map<String, Object>) exchange.getAttribute("parameters");
		final String response = this.getResponse();
		exchange.sendResponseHeaders(200, response.length());
		final OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}