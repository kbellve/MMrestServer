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
public class SetPosition extends Handler {

	private class MissingKeyException extends Exception {
	}

	@Override
	protected String getResponse() throws IOException {
		mmWeb.core.logMessage("µmWeb: Generating SET X, Y or Z position response.");

		Double dXpos = 0.0, dYpos = 0.0, dZpos = 0.0;

		try {
			this.message = new Message("OK");
			if (!this.params.containsKey("x") && !this.params.containsKey("y") && !this.params.containsKey("z")
					&& !this.params.containsKey("Home") && !this.params.containsKey("Origin")) {
				throw new MissingKeyException();
			}

			dXpos = mmWeb.core.getXPosition();
			dYpos = mmWeb.core.getYPosition();
			dZpos = mmWeb.core.getPosition();

			if (this.params.containsKey("X")) {
				dXpos = Double.parseDouble(this.params.get("x").toString());
				mmWeb.core.logMessage("µmWeb: Setting X Postion to: " + dXpos);
				mmWeb.core.setXYPosition(dXpos, dYpos);
			}
			if (this.params.containsKey("Y")) {
				dYpos = Double.parseDouble(this.params.get("y").toString());
				mmWeb.core.logMessage("µmWeb: Setting Y Postion to: " + dYpos);
				mmWeb.core.setXYPosition(dXpos, dYpos);
			}
			if (this.params.containsKey("Z")) {
				dZpos = Double.parseDouble(this.params.get("z").toString());
				mmWeb.core.logMessage("µmWeb: Setting Z Postion to: " + dZpos);
				mmWeb.core.setPosition(dZpos);
			}

			if (this.params.containsKey("Home")) {
				if (this.params.get("Home").toString() == "xy") {
					mmWeb.core.home(mmWeb.core.getXYStageDevice());
				}
				if (this.params.get("Home").toString() == "z") {
					mmWeb.core.home(mmWeb.core.getFocusDevice());
				}
			}

			if (this.params.containsKey("Origin")) {
				if (this.params.get("Origin").toString() == "xy") {
					mmWeb.core.setOriginXY();
				}
				if (this.params.get("Origin").toString() == "z") {
					mmWeb.core.setOrigin();
				}
			}

		} catch (final MissingKeyException e) {
			this.message = new Message("ERROR");
			this.message.error = "SetPosition requests requires either the fields 'x', 'y', 'z','HOME' or 'ORIGIN' to be set.";
		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle SET POSITION request.";
			mmWeb.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}
}