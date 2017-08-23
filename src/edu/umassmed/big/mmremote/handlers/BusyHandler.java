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
public class BusyHandler extends Handler {

	@Override
	protected String getResponse() throws IOException {
		// This function checks to see if a particular device is busy if passed
		// with a device=, using deviceBusy()
		// if nothing is passed, then the systemBusy() is called which will
		// return true if any device is busy

		try {
			this.message = new Message("FALSE");

			// These MM functions seem to be blocking, but it might be device
			// dependent if it is blocking or not...
			if (this.params.containsKey("device")) {
				final String name = this.params.get("device").toString();
				mmWeb.core.logMessage("µmWeb: Checking if " + name + " is busy");
				if (mmWeb.core.deviceBusy(name) == true) {
					this.message = new Message("TRUE");
				} else {
					this.message = new Message("FALSE");
				}
			} else {
				mmWeb.core.logMessage("µmWeb: Checking if µManager is busy");
				if (mmWeb.core.systemBusy() == true) {
					this.message = new Message("TRUE");
				} else {
					this.message = new Message("FALSE");
				}
			}

		} catch (final org.micromanager.internal.utils.MMException e) {
			this.message = new Message("ERROR");
			this.message.error = "GET BUSY failed";
		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle GET BUSY request.";
			mmWeb.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}

}