package edu.umassmed.big.mmremote;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.micromanager.MenuPlugin;
import org.micromanager.Studio;
import org.micromanager.acquisition.SequenceSettings;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.display.DisplayWindow;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

import mmcorej.CMMCore;

/**
 * Implement a REST service hook into MicroManager. This enables external
 * applications to retrieve information about the running system, and perhaps
 * some day manipulate it.
 *
 * @author Matthijs Dorst, Karolinska Institutet, Stockholm, Sweden.
 * @author Karl Bellve, Biomedical Imaging Group, University of Massachusetts
 *         Medical School, Worcester, MA USA
 */

@Plugin(type = MenuPlugin.class)
public class mmKNIME implements MenuPlugin, SciJavaPlugin {

	public static CMMCore core;
	public static final String menuName = "µmKNIME";

	// Provides access to the MicroManager API.
	public static Studio si;
	public static final String tooltipDescription = "Web Gateway between KNIME and µManager";

	public static Coords createCoordinates(final Map<String, Object> params) throws IOException {

		try {

			if (params == null) {
				return null;
			}

			int channel = 0, position = 0, time = 0, z = 0;

			if (params.containsKey("z") || params.containsKey("time") || params.containsKey("channel")
					|| params.containsKey("position")) {
				mmKNIME.core.logMessage("µmKNIME: Created coordinates");

				if (params.containsKey("z")) {
					z = Integer.parseInt(params.get("z").toString());
				}
				if (params.containsKey("time")) {
					time = Integer.parseInt(params.get("time").toString());
				}
				if (params.containsKey("channel")) {
					channel = Integer.parseInt(params.get("channel").toString());
				}
				if (params.containsKey("position")) {
					position = Integer.parseInt(params.get("position").toString());
				}

				Coords.CoordsBuilder builder = mmKNIME.si.data().getCoordsBuilder();
				builder = builder.z(z).time(time).channel(channel).stagePosition(position);
				return (builder.build());
			}

		} catch (final Exception e) {
			mmKNIME.si.getLogManager().showError(e);
		}

		return null;
	}

	public static Datastore createDatastore(final Map<String, Object> params) throws IOException {

		Datastore store = null;

		try {

			boolean bSplit = false, bMetadata = false, bManage = false;
			String sDirectory = "/tmp";

			if (params.containsKey("metadata")) {
				if (params.get("metadata").toString().equalsIgnoreCase("true")) {
					bMetadata = true;
				} else {
					bMetadata = false;
				}
			}

			if (params.containsKey("split")) {
				if (params.get("metadata").toString().equalsIgnoreCase("true")) {
					bSplit = true;
				} else {
					bSplit = false;
				}
			}

			if (params.containsKey("manage")) {
				if (params.get("manage").toString().equalsIgnoreCase("true")) {
					bManage = true;
				} else {
					bManage = false;
				}
			}

			if (params.containsKey("multitiff")) {
				if (params.containsKey("directory")) {
					sDirectory = params.get("directory").toString();
				}
				store = mmKNIME.si.data().createMultipageTIFFDatastore(sDirectory, bMetadata, bSplit);
				mmKNIME.core.logMessage("µmKNIME: Created a Multipage TIFF datastore");
			} else if (params.containsKey("singletiff")) {
				if (params.containsKey("directory")) {
					sDirectory = params.get("directory").toString();
				}
				store = mmKNIME.si.data().createSinglePlaneTIFFSeriesDatastore(sDirectory);
				mmKNIME.core.logMessage("µmKNIME: Created a Single Plane TIFF datastore");

			} else { // default data store
				store = mmKNIME.si.data().createRAMDatastore();
				mmKNIME.core.logMessage("µmKNIME: Created a RAM datastore");
			}

			if (store != null) {
				if (bManage == true) {
					// DisplayWindow display;
					// display = mmKNIME.si.displays().createDisplay(store);
					mmKNIME.si.displays().manage(store);
				}
			} else {
				throw new org.micromanager.internal.utils.MMException("Failed to create Datastore");
			}

			return store;

		} catch (final Exception e) {
			mmKNIME.si.getLogManager().showError(e);
		}

		return null;
	}

	public static DisplayWindow findDisplay(final Map<String, Object> params) throws IOException {

		DisplayWindow display = null;

		try {
			if (params == null) {
				return null;
			}

			if (params.containsKey("title")) {
				// lets try and find window matching title and use that as a
				// display
				final Iterator ImageWindow = mmKNIME.si.getDisplayManager().getAllImageWindows().iterator();

				while (ImageWindow.hasNext()) {
					display = (DisplayWindow) ImageWindow.next();
					if (params.get("title").toString().equals(display.getName())) {
						mmKNIME.core.logMessage("µmKNIME: Found Display Window matching title.");
						return (display);
					} else {
						display = null;
						mmKNIME.core.logMessage("µmKNIME: Failed to find Display Window matching title.");
					}
				}
			}
		} catch (final Exception e) {
			mmKNIME.si.getLogManager().showError(e);
		}

		return null;

	}

	public static SequenceSettings ModifyAcquisitionSettings(final Map<String, Object> params) throws IOException {

		final SequenceSettings settings = mmKNIME.si.acquisitions().getAcquisitionSettings();

		try {

			if (params == null) {
				return null;
			}

			if (settings != null) {
				if (params.containsKey("root")) {
					settings.root = params.get("root").toString();
					mmKNIME.core.logMessage("µmKNIME: root " + settings.root);
				}

				if (params.containsKey("prefix")) {
					settings.prefix = params.get("prefix").toString();
					mmKNIME.core.logMessage("µmKNIME: prefix " + settings.prefix);
				}

				if (params.containsKey("comment")) {
					settings.comment = params.get("comment").toString();
				}

				if (params.containsKey("save")) {
					if (params.get("save").toString().equalsIgnoreCase("false")) {
						settings.save = false;
					} else {
						settings.save = true;
					}
				}

				if (params.containsKey("relativeZ")) {
					if (params.get("relativeZSlice").toString().equalsIgnoreCase("false")) {
						settings.relativeZSlice = false;
					} else {
						settings.relativeZSlice = true;
					}
				}

				if (params.containsKey("slicesFirst")) {
					if (params.get("slicesFirst").toString().equalsIgnoreCase("false")) {
						settings.slicesFirst = false;
					} else {
						settings.slicesFirst = true;
					}
				}

				if (params.containsKey("timeFirst")) {
					if (params.get("timeFirst").toString().equalsIgnoreCase("false")) {
						settings.timeFirst = false;
					} else {
						settings.timeFirst = true;
					}
				}

				if (params.containsKey("autofocus")) {
					if (params.get("autofocus").toString().equalsIgnoreCase("false")) {
						settings.useAutofocus = false;
					} else {
						settings.useAutofocus = true;
					}
				}

				if (params.containsKey("skipautofocus")) {
					settings.skipAutofocusCount = Integer.parseInt(params.get("skipautofocus").toString());
				}

				if (params.containsKey("display")) {
					if (params.get("display").toString().equalsIgnoreCase("false")) {
						settings.shouldDisplayImages = false;
					} else {
						settings.shouldDisplayImages = true;
					}
				}

				if (params.containsKey("frames")) {
					settings.numFrames = Integer.parseInt(params.get("frames").toString());
				}

				if (params.containsKey("cameraTimeout")) {
					settings.cameraTimeout = Integer.parseInt(params.get("cameraTimeout").toString());
				}

				if (params.containsKey("interval")) {
					settings.intervalMs = Double.parseDouble(params.get("interval").toString());
				}
				/*
				 * if (params.containsKey("z")) { final Object obj =
				 * params.get("z"); settings.slices.clear(); if (obj instanceof
				 * List<?>) { mmKNIME.core.logMessage("µmKNIME: List: " +
				 * ((List<String>) obj).size());
				 *
				 * for (int x = 0; x < ((List<String>) obj).size(); x++) {
				 * mmKNIME.core.logMessage("µmKNIME: List: " + ((List<String>)
				 * obj).get(x).toString());
				 *
				 * settings.slices.add(Double.parseDouble(((List<String>)
				 * obj).get(x).toString())); } } else if (obj instanceof String)
				 * { mmKNIME.core.logMessage("µmKNIME: String");
				 * settings.slices.add(Double.parseDouble(params.get("z").
				 * toString())); }
				 *
				 * }
				 */

				if (params.containsKey("startZ") && params.containsKey("endZ") && params.containsKey("stepZ")) {
					settings.slices.clear();
					settings.slices.add(Double.parseDouble(params.get("startZ").toString()));
					// Step Z seems to be stored not as a pure Z Step, but Z
					// Step +
					// Start Z..odd
					settings.slices.add(Double.parseDouble(params.get("stepZ").toString())
							+ Double.parseDouble(params.get("startZ").toString()));
					settings.slices.add(Double.parseDouble(params.get("endZ").toString()));
				} else if (params.containsKey("startZ") || params.containsKey("endZ") || params.containsKey("stepZ")) {
					mmKNIME.core.logMessage("µmKNIME: must pass startZ, endZ and stepZ");
				}

				mmKNIME.core.logMessage("µmKNIME: Resetting modified settings");
				mmKNIME.si.acquisitions().setAcquisitionSettings(settings);
			}

		} catch (final Exception e) {
			mmKNIME.si.getLogManager().showError(e);
		}

		return settings;
	}

	@Override
	public String getCopyright() {
		return "Matthijs Dorst and Karl Bellve under MIT License";
	}

	@Override
	public String getHelpText() {
		return mmKNIME.tooltipDescription;
	}

	@Override
	public String getName() {
		return mmKNIME.menuName;
	}

	@Override
	public String getSubMenu() {
		return "Beta";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public void onPluginSelected() {
		mmKNIME.core.logMessage("µmKNIME: Ready for control by KNIME, active on port 8000.");
		try {
			Service.start();
		} catch (final Exception e) {
			mmKNIME.si.getLogManager().logError(e);
		}
	}

	@Override
	public void setContext(final Studio studio) {
		mmKNIME.si = studio;
		mmKNIME.core = studio.getCMMCore();
	}

}