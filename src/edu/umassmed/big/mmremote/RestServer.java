package edu.umassmed.big.mmremote;

import mmcorej.CMMCore;
import org.micromanager.api.ScriptInterface;
import org.micromanager.utils.ReportingUtils;

/**
 *  Implement a REST service hook into MicroManager. This enables external
 *  applications to retrieve information about the running system, and perhaps
 *  some day manipulate it.
 * 
 *  @author Matthijs Dorst, Karolinska Institutet, Stockholm, Sweden.
 *	@author Karl Bellve, Biomedical Imaging Group, University of Massachusetts Medical School, Worcester, MA USA
 */
public class RestServer implements org.micromanager.api.MMPlugin {

    public static ScriptInterface   si      = null;
    public static CMMCore           core    = null;
    

    @Override
    public void setApp(ScriptInterface si) {
        ReportingUtils.logMessage("Ready for control by KNIME, active on port 8000.");
        RestServer.si   = si;
        RestServer.core = si.getMMCore();
        try {
            Service.start();
        } catch (Exception e) {
            ReportingUtils.showError(e);
        }
    }

    @Override
    public void show() {
        
    }
    
    
    /**
     *  General purpose information members.
     */
    @Override public String getDescription() { return "Allows KNIME to control MM via KNIME REST Nodes"; }
    @Override public String getInfo()        { return "REST-full interface server.";        }
    @Override public String getVersion()     { return "1";               }
    @Override public String getCopyright()   { return "Matthijs Dorst and Karl Bellve under MIT License";  }
    @Override public void dispose()          {                           }    
}