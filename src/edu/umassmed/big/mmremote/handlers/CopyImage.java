package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;
import java.util.List;

import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.display.DisplayWindow;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmWeb;

/**
 *
 * @author kdb
 */
public class CopyImage extends Handler {

	@Override
	protected String getResponse() throws IOException {

		mmWeb.core.logMessage("µmWeb: Copying image to new window with datastore.");
		Datastore saveDatastore = null;
		DisplayWindow currentDisplay = null;

		try {
			this.message = new Message("OK");

			// Get Current Window
			// DisplayWindow currentDisplay =
			// mmWeb.si.displays().getCurrentWindow();
			if (currentDisplay == null) {
				currentDisplay = mmWeb.si.live().getDisplay();
			}

			// Retrieve all the images from current display window
			final List<Image> currentImages = currentDisplay.getDisplayedImages();

			// find window matching title from URL, if it exists
			DisplayWindow saveDisplay = mmWeb.findDisplay(this.params);

			// failed to find existing display for saving images, so we need to
			// create one.
			if (saveDisplay == null) {
				saveDatastore = mmWeb.createDatastore(this.params);
				if (saveDatastore != null) {
					saveDisplay = mmWeb.si.displays().createDisplay(saveDatastore);
				} else {
					this.message = new Message("ERROR");
					this.message.error = "Failed to find or create a new datastore for saving images.";
					mmWeb.core.logMessage("µmWeb: Failed to find or create a new datastore for saving images.");
				}
			}

			if (saveDisplay != null) {
				if (this.params.containsKey("title")) {
					// Title maybe already set, but setting it again in case it
					// is a new display
					saveDisplay.setCustomTitle(this.params.get("title").toString());
				}

				// retrieve its datastore
				saveDatastore = saveDisplay.getDatastore();

				// transfer images into the save datastore
				if ((currentImages.isEmpty() == false) && (saveDatastore != null)) {
					for (int x = 0; x < currentImages.size(); x++) {
						Image newImage = currentImages.get(x);
						Coords coords = mmWeb.createCoordinates(this.params);
						if (coords == null) {
							mmWeb.core.logMessage("µmWeb: no coordinates passed on URL, adding as another timepoint");
							coords = newImage.getCoords();
							final Integer time = saveDatastore.getAxisLength("time");
							newImage = newImage.copyAtCoords(coords.copy().time(time).build());
						} else {
							mmWeb.core.logMessage("µmWeb: coordinates passed on URL");
							newImage = newImage.copyAtCoords(coords);
						}

						// need to add to store, rather than overwrite.
						// by default, stores will not allow overwriting.
						saveDatastore.putImage(newImage);
					}
				}
			} else {
				this.message = new Message("ERROR");
				this.message.error = "Failed to find or create a new display window for saving images.";
				mmWeb.core.logMessage("µmWeb: Failed to find or create a new display window for saving images.");
			}

		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle Save Image request.";
			mmWeb.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}

}
