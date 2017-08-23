package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.umassmed.big.mmremote.mmWeb;

/**
 * Handle loading a page. In essence, this is an abstract wrapper around a file
 * reader for a single HTML page. Extending classes should implement the
 * getFileStream method, and optionally load additional data required for
 * displaying the file.
 *
 * @author Matthijs
 */
abstract public class PageHandler implements HttpHandler {

	abstract protected InputStream getFileStream();

	@Override
	public void handle(final HttpExchange exchange) throws IOException {
		mmWeb.core.logMessage("µmWeb: Handling overview request.");

		final java.util.Scanner scanner = new java.util.Scanner(this.getFileStream()).useDelimiter("\\A");
		final String response = scanner.hasNext() ? scanner.next() : "";
		exchange.sendResponseHeaders(200, response.length());
		final OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}