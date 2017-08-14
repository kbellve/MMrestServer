package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import org.micromanager.data.Coords;
import org.micromanager.display.DisplayWindow;

import edu.umassmed.big.mmremote.mmKNIME;
import ij.ImagePlus;

/**
 * Handle GET (retrieval) requests. These should never modify anything, only
 * provide information. This abstract base class should be extended by classes
 * that provide retrieval functionality.
 *
 * @author kdb
 */
public class GetImage extends ImageHandler {

	@Override
	public ImagePlus getResponse() throws IOException {

		final DisplayWindow display = mmKNIME.findDisplay(this.params);

		if (display != null) {
			final Coords coord = mmKNIME.createCoordinates(this.params);
			if (coord != null) {
				mmKNIME.core.logMessage("µmKNIME: Found coordinates");
				display.setDisplayedImageTo(coord);
			}

			// I know this is lazy, but this is the simplest way to get an
			// ImagePlus image from a datastore, which uses µManager Image.
			return (display.getImagePlus());

		} else {
			return (mmKNIME.si.getSnapLiveManager().getDisplay().getImagePlus());
		}
	}
}