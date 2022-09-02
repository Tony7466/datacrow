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
import net.datacrow.core.IconLibrary;
import net.datacrow.core.console.IMasterView;
import net.datacrow.core.console.IView;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.resources.DcResources;

public class EditItem extends Plugin {

	private static final long serialVersionUID = -8818198030805572238L;

	public EditItem(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
        super(dco, template, viewIdx, moduleIdx, viewType);
    } 
    
    @Override
    public boolean isAdminOnly() {
        return false;
    }
    
    @Override
    public boolean isAuthorizable() {
        return false;
    }    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        IMasterView mv = GUI.getInstance().getSearchView(getModule().getIndex());
        IView view = mv.getCurrent();
        view.open(false);
    }

    @Override
    public boolean isSystemPlugin() {
        return true;
    }

    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoOpen;
    }

    @Override
    public String getLabelShort() {
        return DcResources.getText("lblEditItem");
    }

    @Override
    public String getLabel() {
    	if (getModule() != null)
    		return DcResources.getText("lblEditItem", getModule().getObjectName());

		return "";
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("tpEditItem");
    }  
}
