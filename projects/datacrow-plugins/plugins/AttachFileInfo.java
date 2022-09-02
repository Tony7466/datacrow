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
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.windows.BrowserDialog;
import net.datacrow.console.windows.itemforms.ItemForm;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.UserMode;
import net.datacrow.core.clients.IFileImportClient;
import net.datacrow.core.fileimporter.FileImporter;
import net.datacrow.core.fileimporter.FileImporters;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.services.Region;
import net.datacrow.core.services.SearchMode;
import net.datacrow.core.services.plugin.IServer;
import net.datacrow.core.utilities.filefilters.DcFileFilter;

import org.apache.log4j.Logger;

public class AttachFileInfo extends Plugin implements IFileImportClient {
    
	private static final long serialVersionUID = -8096220110268580352L;

	private static Logger logger = Logger.getLogger(AttachFileInfo.class.getName());
    
    public AttachFileInfo(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
        super(dco, template, viewIdx, moduleIdx, viewType);
    }
    
    @Override
    public boolean isAdminOnly() {
        return false;
    }
    
    @Override
    public boolean isAuthorizable() {
        return true;
    }   
    
    @Override
    public int getXpLevel() {
        return UserMode._XP_EXPERT;
    }
    
    @Override
	public boolean isEnabled() {
    	Connector connector = DcConfig.getInstance().getConnector();
		return connector.getUser().isEditingAllowed(getModule());
	}    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        DcObject dco;
        ItemForm form = null;
        if (GUI.getInstance().getRootFrame() instanceof ItemForm) {
            form = (ItemForm) GUI.getInstance().getRootFrame();
            dco = form.getItem();
        } else {
            DcModule module = getModule().getChild() != null ? getModule().getChild() : getModule();
            dco = GUI.getInstance().getSearchView(module.getIndex()).getCurrent().getSelectedItem();            
        }
        
        FileImporters importers = FileImporters.getInstance();
        if (!importers.hasImporter(dco.getModuleIdx())) return;
        FileImporter importer = importers.getFileImporter(dco.getModuleIdx());
        
        String[] extensions = importer.getSupportedFileTypes();
        DcFileFilter filter = extensions.length > 0 ? new DcFileFilter(extensions) : null;
        BrowserDialog dlg = new BrowserDialog(DcResources.getText("lblSelectFile"), filter);
        File file = dlg.showOpenFileDialog(form != null ? (JFrame) form : 
            							  (JFrame) GUI.getInstance().getMainFrame(), 
                                           dco.getFilename() != null ? new File(dco.getFilename()) : null );
        String filename = file != null ? file.toString() : null;
        
        if (filename != null && filename.length() > 0) {
            // overwrite empty information
            importer.setClient(this);
            DcObject dcoNew = importer.parse(filename, 0);
            if (dcoNew.getModule().getIndex() != dco.getModule().getIndex()) {
                if (dcoNew.getChildren() != null)
                    for (DcObject child : dcoNew.getChildren()) dcoNew = child;
            }
            
            dco.copy(dcoNew, false, false);
            
            // overwrite parsed technical information, images and the file information
            for (DcField field : dcoNew.getFields()) {
                if (field.getValueType() == DcRepository.ValueTypes._PICTURE) {
                    Picture picture = (Picture) dcoNew.getValue(field.getIndex());
                    if (picture != null)
                        dco.setValue(field.getIndex(), new DcImageIcon(picture.getImage()));
                } else if (field.getFieldType() == ComponentFactory._FILEFIELD ||
                           field.getFieldType() == ComponentFactory._FILELAUNCHFIELD) {
                    dco.setValue(field.getIndex(), filename);
                }
            }
            
            if (form != null) {
                form.setData(dco, true, false);
            } else {
                if (dco.isChanged()) {
                    try {
                    	DcConfig.getInstance().getConnector().saveItem(dco);
                    } catch (ValidationException ve) {
                        GUI.getInstance().displayWarningMessage(ve.getMessage());
                    }
                }
            }
                
        }
    }
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }
   
    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK);        
    }
    
    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoImport;
    }
    
    @Override
    public String getLabelShort() {
        return DcResources.getText("lblReadFileInfo");
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblReadFileInfo");
    }

    @Override
    public void notifyError(Throwable e) {
        logger.error(e, e);
    }

    @Override
    public void notify(String message) {
        logger.info(message);
    }
    
    @Override
    public void notifyWarning(String msg) {
        logger.warn(msg);
    }

    @Override
    public DcObject getContainer() {
        return null;
    }

    @Override
    public int getDirectoryUsage() {
        return -1;
    }

    @Override
    public Region getRegion() {
        return null;
    }

    @Override
    public SearchMode getSearchMode() {
        return null;
    }

    @Override
    public IServer getServer() {
        return null;
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("tpAttachFileInfo");
    }

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public void notifyProcessed() {}

    @Override
    public void notifyTaskCompleted(boolean success, String taskID) {}

    @Override
    public void notifyTaskStarted(int taskSize) {}

    @Override
    public boolean useOnlineServices() {
        return false;
    }

    @Override
    public DcObject getStorageMedium() {
        return null;
    }
}