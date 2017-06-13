
package edu.umassmed.big.mmremote.handlers;

import java.util.Map;

import org.micromanager.utils.ReportingUtils;

import edu.umassmed.big.mmremote.RestServer;

/**
*  Scan parms for uManager commands and change them in uManager.
*  This should allow any command to set, like snap image, uManager variables, like exposure time.
*
*  @author kdb
*/

public class SetParameter  {

     protected void parse(Map<String, Object>  params) throws Exception {
    	 try
         {
            if (params.containsKey("X") || params.containsKey("Y") || params.containsKey("Z") || params.containsKey("Home")) {

                    double dXpos = RestServer.core.getXPosition();
                    double dYpos = RestServer.core.getYPosition();
                    double dZpos = RestServer.core.getPosition();

                    if (params.containsKey("X")) {
                            dXpos  = Double.parseDouble(params.get("X").toString());
                            ReportingUtils.logMessage("Setting X Postion to: " + dXpos);
                            RestServer.core.setXYPosition(dXpos, dYpos);
                    }
                    if (params.containsKey("Y")) {
                            dYpos  = Double.parseDouble(params.get("Y").toString());
                            ReportingUtils.logMessage("Setting Y Postion to: " + dYpos);
                            RestServer.core.setXYPosition(dXpos, dYpos);
                    }
                    if (params.containsKey("Z")) {
                            dZpos  = Double.parseDouble(params.get("Z").toString());
                            ReportingUtils.logMessage("Setting Z Postion to: " + dZpos);
                            RestServer.core.setPosition(dZpos);
                    }
            }

            if (params.containsKey("Home")) {
                    if (params.get("Home").toString() == "XY")
                            RestServer.core.home(RestServer.core.getXYStageDevice());
                    if (params.get("Home").toString() == "Z")
                            RestServer.core.home(RestServer.core.getFocusDevice());
            }

            if (params.containsKey("Origin")) {
                    if (params.get("Origin").toString() == "XY")
                            RestServer.core.setOriginXY();
                    if (params.get("Origin").toString() == "Z")
                            RestServer.core.setOrigin();
            }

        }
             catch (org.micromanager.utils.MMScriptException e) {
                     ReportingUtils.logError(e);
             }
             catch (Exception e) {
                     ReportingUtils.logError(e);
        }
    }
}
