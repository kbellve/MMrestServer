package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.display.DisplayWindow;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmKNIME;
import mmcorej.TaggedImage;

/**
 *
 * @author kdb
 */
public class SnapImageHandler extends Handler {

	@Override
	protected String getResponse() throws IOException {
		mmKNIME.core.logMessage("µmKNIME: Generating Snap Image response.");

		try {
			Datastore store = null;
			DisplayWindow display = null;
			this.message = new Message("OK");

			// Optionally change exposure before snapping image
			if (this.params.containsKey("exposure")) {
				final double exposure = Double.parseDouble(this.params.get("exposure").toString());
				mmKNIME.core.setExposure(exposure);
			}

			display = mmKNIME.findDisplay(this.params);

			// check if we have any coordinates first, if we have no
			// coordinates, then just snap a live image
			final Coords coord = mmKNIME.createCoordinates(this.params);
			if (coord == null) {
				mmKNIME.core.logMessage("µmKNIME: Snapping a live image since no coordinates were passed.");
				mmKNIME.si.live().snap(true);
				display = mmKNIME.si.live().getDisplay();
			} else {
				mmKNIME.core.logMessage("µmKNIME: Snapping a single image.");
				mmKNIME.core.snapImage();
				if (display == null) {
					mmKNIME.core.logMessage("µmKNIME: Creating new datastore...");
					store = mmKNIME.createDatastore(this.params);
					if (store != null) {
						mmKNIME.core.logMessage("µmKNIME: Creating new window with attached datastore...");
						display = mmKNIME.si.displays().createDisplay(store);
					}
				} else {
					store = display.getDatastore();
				}

				if (store != null) {
					if (store.getImage(coord) != null) {
						mmKNIME.core
								.logMessage("µmKNIME: Datastore does not allow overwrites at the same coordinates...");
					} else {
						mmKNIME.core.logMessage("µmKNIME: Adding new image to datastore...");
						final TaggedImage tmp = mmKNIME.core.getTaggedImage();
						if (tmp != null) {
							mmKNIME.core.logMessage("µmKNIME: Adding new tagged image to datastore...");
							Image image = mmKNIME.si.data().convertTaggedImage(tmp);
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
						mmKNIME.core.logMessage("µmKNIME: Changing title of window");
						display.setCustomTitle(this.params.get("title").toString());
					}
				}
			}

		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle Snap Image request.";
			mmKNIME.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}

}
