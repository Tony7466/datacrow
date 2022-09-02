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
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ImageIcon;

import net.datacrow.console.windows.ItemTypeDialog;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.UserMode;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.resources.DcResources;

public class ItemImporterWizard extends Plugin {

	private static final long serialVersionUID = 6786432836551770563L;

	public ItemImporterWizard(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
        super(dco, template, viewIdx, moduleIdx, viewType);
    }   
    
    @Override
    public boolean isAdminOnly() {
        return true;
    }
    
    @Override
    public boolean isAuthorizable() {
        return false;
    }    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        DcModule module = DcModules.getCurrent();
        Collection<DcModule> modules = new ArrayList<DcModule>();
        modules.add(module);
        modules.addAll(DcModules.getReferencedModules(module.getIndex()));
        
        ItemTypeDialog dlg = new ItemTypeDialog(modules, DcResources.getText("msgSelectModuleImport"));
        dlg.setVisible(true);

        int moduleIdx = dlg.getSelectedModule();
        
        if (moduleIdx > 0)
            new net.datacrow.console.wizards.itemimport.ItemImporterWizard(moduleIdx).setVisible(true);
    }
    
    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoItemImport16;
    }
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblItemImportWizard");
    }
    
    @Override
    public int getXpLevel() {
        return UserMode._XP_EXPERT;
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("tpItemImportWizard");
    }      
}
