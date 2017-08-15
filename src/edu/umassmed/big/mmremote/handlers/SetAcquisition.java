package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

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
			this.message = new Message("OK");
			if (this.params.containsKey("stop")) {
				mmKNIME.core.logMessage("µmKNIME: Stopping all non-blocking MD Acquisitions");
				mmKNIME.si.acquisitions().haltAcquisition();
			} else {
				if (this.params.containsKey("settings")) {
					mmKNIME.core.logMessage("µmKNIME: Loading saved MD acquistion settings");
					mmKNIME.si.acquisitions().loadAcquisition(this.params.get("settings").toString());
				}

				mmKNIME.ModifyAcquisitionSettings(this.params);

				if (this.params.containsKey("run")) {
					mmKNIME.si.acquisitions().runAcquisitionNonblocking();
					mmKNIME.core.logMessage("µmKNIME: Running a non-blocking MD acquisition");
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