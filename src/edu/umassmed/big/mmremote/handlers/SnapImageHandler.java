package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.display.DisplayWindow;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmWeb;
import mmcorej.TaggedImage;

/**
 *
 * @author kdb
 */
public class SnapImageHandler extends Handler {

	@Override
	protected String getResponse() throws IOException {
		mmWeb.core.logMessage("µmWeb: Generating Snap Image response.");

		try {
			Datastore store = null;
			DisplayWindow display = null;
			this.message = new Message("OK");

			// Optionally change exposure before snapping image
			if (this.params.containsKey("exposure")) {
				final double exposure = Double.parseDouble(this.params.get("exposure").toString());
				mmWeb.core.setExposure(exposure);
			}

			display = mmWeb.findDisplay(this.params);

			// check if we have any coordinates first, if we have no
			// coordinates, then just snap a live image
			final Coords coord = mmWeb.createCoordinates(this.params);
			if (coord == null) {
				mmWeb.core.logMessage("µmWeb: Snapping a live image since no coordinates were passed.");
				mmWeb.si.live().snap(true);
				display = mmWeb.si.live().getDisplay();
			} else {
				mmWeb.core.logMessage("µmWeb: Snapping a single image.");
				mmWeb.core.snapImage();
				if (display == null) {
					mmWeb.core.logMessage("µmWeb: Creating new datastore...");
					store = mmWeb.createDatastore(this.params);
					if (store != null) {
						mmWeb.core.logMessage("µmWeb: Creating new window with attached datastore...");
						display = mmWeb.si.displays().createDisplay(store);
					}
				} else {
					store = display.getDatastore();
				}

				if (store != null) {
					if (store.getImage(coord) != null) {
						mmWeb.core.logMessage("µmWeb: Datastore does not allow overwrites at the same coordinates...");
					} else {
						mmWeb.core.logMessage("µmWeb: Adding new image to datastore...");
						final TaggedImage tmp = mmWeb.core.getTaggedImage();
						if (tmp != null) {
							mmWeb.core.logMessage("µmWeb: Adding new tagged image to datastore...");
							Image image = mmWeb.si.data().convertTaggedImage(tmp);
							image = image.copyAtCoords(coord);

							if (image != null) {
								store.putImage(image);
							}
						}
					}
				}
			}

			if (this.params != null) {
				if (this.params.containsKey("title")) {

					if (display != null) {
						mmWeb.core.logMessage("µmWeb: Changing title of window");
						display.setCustomTitle(this.params.get("title").toString());
					}
				}
			}

		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle Snap Image request.";
			mmWeb.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}

}
