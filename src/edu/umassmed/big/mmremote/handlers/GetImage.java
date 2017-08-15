package edu.umassmed.big.mmremote.handlers;

import java.io.IOException;

import org.micromanager.data.Coords;
import org.micromanager.display.DisplayWindow;

import edu.umassmed.big.mmremote.mmKNIME;
import ij.ImagePlus;
import ij.process.ImageProcessor;

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

		DisplayWindow display = mmKNIME.findDisplay(this.params);
		ImageProcessor imageprocessor = null;
		ImagePlus imageplus = null;

		try {
			if (display == null) {
				display = mmKNIME.si.getSnapLiveManager().getDisplay();
			}

			final Coords coord = mmKNIME.createCoordinates(this.params);
			if (coord != null) {
				display.setDisplayedImageTo(coord);
				imageprocessor = mmKNIME.si.data().ij().createProcessor(display.getDatastore().getImage(coord));
			} else {
				imageprocessor = mmKNIME.si.data().ij().createProcessor(display.getDatastore().getAnyImage());
			}

			if (imageprocessor != null) {
				imageplus = new ImagePlus(display.getName(), imageprocessor);
				if (imageplus != null) {
					return (imageplus);
				}
			}
		} catch (final Exception e) {
			mmKNIME.si.getLogManager().showError(e);
		}

		return null;

	}

}