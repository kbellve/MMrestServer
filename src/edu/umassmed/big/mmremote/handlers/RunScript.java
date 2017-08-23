package edu.umassmed.big.mmremote.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

import edu.umassmed.big.mmremote.Message;
import edu.umassmed.big.mmremote.mmWeb;

/**
 * Handle change requests.
 *
 * @author kdb
 */
public class RunScript extends Handler {

	private class MissingKeyException extends Exception {
	}

	@Override
	protected String getResponse() throws IOException {
		mmWeb.core.logMessage("µmWeb: Creating an ROI.");

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
					mmWeb.core.logMessage("µmWeb: Executing script " + ScriptFile);
					mmWeb.si.getScriptController().runFile(ScriptFile);
				} else {
					mmWeb.core.logMessage("µmWeb: Script file does not exist");
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
					mmWeb.core.logMessage("µmWeb: Executing script " + script);
					mmWeb.core.logMessage("µmWeb: Executing script " + ScriptFile);
					mmWeb.si.getScriptController().runFile(ScriptFile);
				} else {
					mmWeb.core.logMessage("µmWeb: Script file does not exist");
				}
			}

		} catch (final MissingKeyException e) {
			this.message = new Message("ERROR");
			this.message.error = "SetScript requests requires fields 'file' or 'script'";
		} catch (final Exception e) {
			this.message = new Message("ERROR");
			this.message.error = "Could not handle SET Script request.";
			mmWeb.si.getLogManager().showError(e);
		}
		final Gson gson = new Gson();
		final String response = gson.toJson(this.message);
		return response;
	}
}