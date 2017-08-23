package edu.umassmed.big.mmremote.handlers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import javax.imageio.ImageIO;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.umassmed.big.mmremote.mmWeb;
import ij.ImagePlus;

/**
 * Handle GET (retrieval) requests. These should never modify anything, only
 * provide information. This abstract base class should be extended by classes
 * that provide retrieval functionality.
 *
 * @author kdb
 */
abstract public class ImageHandler implements HttpHandler {

	protected Map<String, Object> params; // URI request parameters.

	abstract protected ImagePlus getResponse() throws IOException;

	@Override
	public void handle(final HttpExchange exchange) throws IOException {

		try {
			mmWeb.core.logMessage("µmKNIME: Image Handling request.");
			this.params = (Map<String, Object>) exchange.getAttribute("parameters");

			final ImagePlus image = this.getResponse();
			if (image != null) {
				final BufferedImage bufferdimage = image.getBufferedImage();

				// Encode image into desired encoding, write it to byte-stream.
				final ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
				ImageIO.write(bufferdimage, "PNG", out);
				out.flush();

				exchange.getResponseHeaders().set("Content-Type", "image/png");
				exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, out.size());

				final OutputStream os = exchange.getResponseBody();
				os.write(out.toByteArray());
				os.flush();

				out.close();
				os.close();
			} else {
				exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
			}

			exchange.close();
		} catch (final Exception e) {
			mmWeb.si.getLogManager().showError(e);
		}

	}
}