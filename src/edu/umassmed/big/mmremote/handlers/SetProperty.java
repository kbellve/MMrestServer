package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmWeb;

/**
 * Handle change requests.
 *
 * @author kdb
 */
public class SetProperty extends Handler {

	private class MissingKeyException extends Exception {
	}

	@Override
	protected String getResponse() throws IOException {
		mmWeb.core.logMessage("µmWeb: Generating SET response.");

		try {
			this.message = new Message("OK");
			if (!this.params.containsKey("device") || !this.params.containsKey("property")
					|| !this.params.containsKey("value")) {
				throw new MissingKeyException();
			}

			final String label = this.params.get("device").toString();
			final String propName = this.params.get("property").toString();
			final String propValue = this.params.get("value").toString();

			mmWeb.core.setProperty(label, propName, Integer.parseInt(propValue));

		} catch (final MissingKeyException e) {
			this.message = new Message("ERROR");
			this.message.error = "SetProperty requests require the fields 'device', 'property' and 'value' to be set.";
		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle SET request.";
			mmWeb.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}
}