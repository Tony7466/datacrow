/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                       This file is part of Data Crow.                      *
 *       Data Crow is free software; you can redistribute it and/or           *
 *        modify it under the terms of the GNU General Public                 *
 *       License as published by the Free Software Foundation; either         *
 *              version 3 of the License, or any later version.               *
 *                                                                            *
 *        Data Crow is distributed in the hope that it will be useful,        *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             *
 *           See the GNU General Public License for more details.             *
 *                                                                            *
 *        You should have received a copy of the GNU General Public           *
 *  License along with this program. If not, see http://www.gnu.org/licenses  *
 *                                                                            *
 ******************************************************************************/

package plugins;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import net.datacrow.console.GUI;
import net.datacrow.console.windows.FindReplaceDialog;
import net.datacrow.core.DcConfig;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

public class FindReplace extends Plugin {

	private static final long serialVersionUID = 114669273363631045L;

	public FindReplace(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
        super(dco, template, viewIdx, moduleIdx, viewType);
    }  
    
    @Override
    public boolean isAdminOnly() {
        return false;
    }
    
    @Override
	public boolean isEnabled() {
        Connector connector = DcConfig.getInstance().getConnector();
		return connector.getUser().isEditingAllowed(getModule());
	}    
    
    @Override
    public boolean isAuthorizable() {
        return true;
    }    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        FindReplaceDialog dlg = GUI.getInstance().getFindReplaceDialog(getModuleIdx());
        dlg.setVisible(true);
    }
    
    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoUpdateAll;
    }
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblFindReplace");
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("tpFindReplace");
    }       
}
