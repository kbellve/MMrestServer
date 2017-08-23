package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmWeb;
import mmcorej.StrVector;

/**
 * Handle change requests.
 *
 * @author kdb
 */
public class GetProperty extends Handler {

	private class MissingKeyException extends Exception {
	}

	@Override
	protected String getResponse() throws IOException {
		mmWeb.core.logMessage("µmWeb: Generating GET response.");

		try {
			this.message = new Message("OK");
			final LinkedTreeMap map = new LinkedTreeMap();
			String value;

			// return all properties
			if (this.params.containsKey("device") && !this.params.containsKey("property")) {
				final String device = this.params.get("device").toString();

				final StrVector properties = mmWeb.core.getDevicePropertyNames(device);

				for (final String property : properties) {
					value = mmWeb.core.getProperty(device, property);
					map.put(property, value);
				}
				this.message.payload.put(device, map);
			} else {
				// or just return a specific property
				if (!this.params.containsKey("device") || !this.params.containsKey("property")) {
					throw new MissingKeyException();
				} else {
					final String device = this.params.get("device").toString();
					final String property = this.params.get("property").toString();

					value = mmWeb.core.getProperty(device, property);
					map.put(property, value);

					this.message.payload.put(device, map);
				}
			}

		} catch (final MissingKeyException e) {
			this.message = new Message("ERROR");
			this.message.error = "GetProperty requests require the fields 'device', and 'property to be set.";
		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle GET request.";
			mmWeb.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}
}