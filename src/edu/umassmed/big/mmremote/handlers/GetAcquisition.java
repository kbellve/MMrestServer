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
public class GetAcquisition extends Handler {

	@Override
	protected String getResponse() throws IOException {
		// This function checks to see if an acquisition is running
		// if nothing is passed, then the systemBusy() is called which will
		// return true if any device is busy

		try {
			this.message = new Message("FALSE");

			mmWeb.core.logMessage("µmWeb: Checking if µManager is running an acquisition");
			if (mmWeb.si.getAcquisitionManager().isAcquisitionRunning() == true) {
				this.message = new Message("TRUE");
			} else {
				this.message = new Message("FALSE");
			}
		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle GET ACQUISITION request.";
			mmWeb.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}

}