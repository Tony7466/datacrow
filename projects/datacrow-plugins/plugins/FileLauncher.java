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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import net.datacrow.console.GUI;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.console.IView;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.resources.DcResources;

public class FileLauncher extends Plugin {
	
	private static final long serialVersionUID = 3811899575126914470L;

	public FileLauncher(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
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
    	DcModule module = getModule().getChild() != null ? getModule().getChild() : getModule();
    	
    	IView view = GUI.getInstance().getSearchView(module.getIndex()).getCurrent();
    	
    	if (view != null) {
            DcObject dco = view.getSelectedItem();
            if (dco == null) 
                return;
            else if (getModule().getIndex() == DcModules._MEDIA) 
            	dco.load(new int[] {DcObject._ID, dco.getFileField().getIndex()});
            
            new net.datacrow.util.launcher.FileLauncher(dco.getFilename()).launch();
    	}
    }
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        DcModule module = getModule().getChild() != null ? getModule().getChild() : getModule();
        
        boolean fileExists = false;
        IView view = GUI.getInstance().getSearchView(module.getIndex()).getCurrent();
        if (view != null) {
            DcObject dco = view.getSelectedItem();
            if (dco != null)
                fileExists = dco.getFilename() != null && new File(dco.getFilename()).exists();
        }
        
        return fileExists;
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK);
    }
    
    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoOpenApplication;
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblStartProgram");
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("tpStartProgram");
    }     
}
