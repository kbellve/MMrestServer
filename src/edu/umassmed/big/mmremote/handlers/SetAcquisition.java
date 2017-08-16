package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import org.micromanager.acquisition.SequenceSettings;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmKNIME;

/**
 * Handle change requests.
 *
 * @author kdb
 */
public class SetAcquisition extends Handler {

	@Override
	protected String getResponse() throws IOException {
		mmKNIME.core.logMessage("µmKNIME: Set MD Acquistion");

		try {

			Boolean bBlocking = true;

			this.message = new Message("OK");
			if (this.params.containsKey("stop")) {
				mmKNIME.core.logMessage("µmKNIME: Stopping all non-blocking MD Acquisitions");
				mmKNIME.si.acquisitions().haltAcquisition();
			} else {
				if (this.params.containsKey("settings")) {
					mmKNIME.core.logMessage("µmKNIME: Loading saved MD acquistion settings");
					mmKNIME.si.acquisitions().loadAcquisition(this.params.get("settings").toString());
				}

				final SequenceSettings settings = mmKNIME.ModifyAcquisitionSettings(this.params);

				if (this.params.containsKey("run")) {
					// mmKNIME.si.acquisitions().runAcquisitionNonblocking();
					if (this.params.containsKey("blocking")) {
						if (this.params.get("blocking").toString().equalsIgnoreCase("false")) {
							mmKNIME.core.logMessage("µmKNIME: Running a non-blocking MD acquisition");
							bBlocking = false;
						} else {
							mmKNIME.core.logMessage("µmKNIME: Running a blocking MD acquisition");
							bBlocking = true;
						}
					}
					mmKNIME.si.acquisitions().runAcquisitionWithSettings(settings, bBlocking);
				}
			}

		} catch (final Exception e) {
			this.message = new Message("ERROR");
			mmKNIME.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}
}