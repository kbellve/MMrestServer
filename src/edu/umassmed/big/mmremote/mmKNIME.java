package edu.umassmed.big.mmremote;

import mmcorej.CMMCore;
import org.micromanager.MenuPlugin;
import org.micromanager.Studio;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

/**
 *  Implement a REST service hook into MicroManager. This enables external
 *  applications to retrieve information about the running system, and perhaps
 *  some day manipulate it.
 * 
 *  @author Matthijs Dorst, Karolinska Institutet, Stockholm, Sweden.
 *	@author Karl Bellve, Biomedical Imaging Group, University of Massachusetts Medical School, Worcester, MA USA
 */

@Plugin(type = MenuPlugin.class)
public class mmKNIME implements MenuPlugin, SciJavaPlugin {
	
	public static final String menuName = "µmKNIME";
	public static final String tooltipDescription = "Web Gateway between KNIME and µManager";

	// Provides access to the MicroManager API.
	public static Studio si;
	public static CMMCore core;	    
    
    @Override
    public void setContext(Studio studio) {
    	mmKNIME.si = studio;
    	mmKNIME.core = studio.getCMMCore();
    }

    @Override
    public void onPluginSelected() {
    	mmKNIME.core.logMessage("µmKNIME: Ready for control by KNIME, active on port 8000.");
        try {
            Service.start();
        } catch (Exception e) {
            mmKNIME.si.getLogManager().logError(e);
        }
    }

    
    @Override public String getName() { return menuName; }
    @Override public String getSubMenu() { return "Beta"; }
    @Override public String getHelpText() { return tooltipDescription; }
    @Override public String getVersion()     { return "0.1";               }
    @Override public String getCopyright()   { return "Matthijs Dorst and Karl Bellve under MIT License";  } 
}