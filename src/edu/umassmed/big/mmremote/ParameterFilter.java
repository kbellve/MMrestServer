package edu.umassmed.big.mmremote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

/**
 * Source: unknown.
 */
public class ParameterFilter extends Filter {

	@Override
	public String description() {
		return "Parses the requested URI for parameters";
	}

	@Override
	public void doFilter(final HttpExchange exchange, final Chain chain) throws IOException {
		this.parseGetParameters(exchange);
		this.parsePostParameters(exchange);
		chain.doFilter(exchange);
	}

	private void parseGetParameters(final HttpExchange exchange) throws UnsupportedEncodingException {

		final Map<String, Object> parameters = new HashMap<String, Object>();
		final URI requestedUri = exchange.getRequestURI();
		final String query = requestedUri.getRawQuery();
		this.parseQuery(query, parameters);
		exchange.setAttribute("parameters", parameters);
	}

	private void parsePostParameters(final HttpExchange exchange) throws IOException {

		if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
			@SuppressWarnings("unchecked")
			final Map<String, Object> parameters = (Map<String, Object>) exchange.getAttribute("parameters");
			final InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			final BufferedReader br = new BufferedReader(isr);
			final String query = br.readLine();
			this.parseQuery(query, parameters);
		}
	}

	@SuppressWarnings("unchecked")
	private void parseQuery(final String query, final Map<String, Object> parameters)
			throws UnsupportedEncodingException {

		if (query != null) {
			final String pairs[] = query.split("[&]");

			for (final String pair : pairs) {
				final String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					final Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						final List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						final List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
}