package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import org.micromanager.acquisition.SequenceSettings;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmWeb;

/**
 * Handle change requests.
 *
 * @author kdb
 */
public class RunAcquisition extends Handler {

	@Override
	protected String getResponse() throws IOException {
		mmWeb.core.logMessage("µmWeb: Set MD Acquistion");

		try {

			Boolean bBlocking = true;

			this.message = new Message("OK");
			if (this.params.containsKey("stop")) {
				mmWeb.core.logMessage("µmWeb: Stopping all non-blocking MD Acquisitions");
				mmWeb.si.acquisitions().haltAcquisition();
			} else {
				if (this.params.containsKey("settings")) {
					mmWeb.core.logMessage("µmWeb: Loading saved MD acquistion settings");
					mmWeb.si.acquisitions().loadAcquisition(this.params.get("settings").toString());
				}

				final SequenceSettings settings = mmWeb.ModifyAcquisitionSettings(this.params);

				if (this.params.containsKey("run")) {
					// mmKNIME.si.acquisitions().runAcquisitionNonblocking();
					if (this.params.containsKey("blocking")) {
						if (this.params.get("blocking").toString().equalsIgnoreCase("false")) {
							mmWeb.core.logMessage("µmWeb: Running a non-blocking MD acquisition");
							bBlocking = false;
						} else {
							mmWeb.core.logMessage("µmWeb: Running a blocking MD acquisition");
							bBlocking = true;
						}
					}
					mmWeb.si.acquisitions().runAcquisitionWithSettings(settings, bBlocking);
				}
			}

		} catch (final Exception e) {
			this.message = new Message("ERROR");
			mmWeb.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}
}