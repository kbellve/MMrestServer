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
public class SetROI extends Handler {

	private class MissingKeyException extends Exception {
	}

	@Override
	protected String getResponse() throws IOException {
		mmWeb.core.logMessage("µmWeb: Creating an ROI.");

		try {
			int x, y, xSize, ySize;

			this.message = new Message("OK");
			if (!this.params.containsKey("x") && !this.params.containsKey("y") && !this.params.containsKey("xSize")
					&& !this.params.containsKey("ySize")) {
				throw new MissingKeyException();
			}

			x = Integer.parseInt(this.params.get("x").toString());

			y = Integer.parseInt(this.params.get("y").toString());

			xSize = Integer.parseInt(this.params.get("xSize").toString());

			ySize = Integer.parseInt(this.params.get("ySize").toString());

			mmWeb.core.logMessage("µmWeb: Setting ROI to (" + x + "," + y + "," + xSize + "," + ySize + ")");

			mmWeb.core.setROI(x, y, xSize, ySize);

		} catch (final MissingKeyException e) {
			this.message = new Message("ERROR");
			this.message.error = "SetROI requests requires the fields 'x', 'y', 'xSize',and 'ySize'";
		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle SET ROI request.";
			mmWeb.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}
}