package edu.umassmed.big.mmremote.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmKNIME;

/**
 * Handle change requests.
 *
 * @author kdb
 */
public class SetScript extends Handler {

	private class MissingKeyException extends Exception {
	}

	@Override
	protected String getResponse() throws IOException {
		mmKNIME.core.logMessage("µmKNIME: Creating an ROI.");

		try {
			this.message = new Message("OK");
			if (!this.params.containsKey("file") && !this.params.containsKey("script")) {
				throw new MissingKeyException();
			}

			if (this.params.containsKey("file")) {
				final String file = this.params.get("file").toString();
				System.out.println(file);
				final File ScriptFile = new File(file);
				if (ScriptFile.exists()) {
					mmKNIME.core.logMessage("µmKNIME: Executing script " + ScriptFile);
					mmKNIME.si.getScriptController().runFile(ScriptFile);
				} else {
					mmKNIME.core.logMessage("µmKNIME: Script file does not exist");
				}
			}
			if (this.params.containsKey("script")) {
				final String script = this.params.get("script").toString();
				System.out.print(script.length());
				final File ScriptFile = File.createTempFile("mm_script", ".bsh");
				ScriptFile.deleteOnExit();
				final BufferedWriter out = new BufferedWriter(new FileWriter(ScriptFile));
				out.write(script);
				out.flush();
				out.close();

				if (ScriptFile.exists()) {
					mmKNIME.core.logMessage("µmKNIME: Executing script " + script);
					mmKNIME.core.logMessage("µmKNIME: Executing script " + ScriptFile);
					mmKNIME.si.getScriptController().runFile(ScriptFile);
				} else {
					mmKNIME.core.logMessage("µmKNIME: Script file does not exist");
				}
			}

		} catch (final MissingKeyException e) {
			this.message = new Message("ERROR");
			this.message.error = "SetScript requests requires fields 'file' or 'script'";
		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle SET Script request.";
			mmKNIME.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}
}