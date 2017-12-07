package edu.umassmed.big.mmremote.handlers;

import java.io.InputStream;

/**
 * Load the overview page.
 * 
 * @author Matthijs
 */
public class IndexHandler extends PageHandler {

	@Override
	protected InputStream getFileStream() {
		// Read file from a resource folder inside the JAR:
		return IndexHandler.class.getResourceAsStream("/edu/umassmed/big/mmremote/resources/overview.html");
	}

}
