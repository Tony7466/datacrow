/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                              info@datacrow.org                             *
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

package org.datacrow.client.console.wizards.itemimport;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.datacrow.client.console.GUI;
import org.datacrow.client.console.Layout;
import org.datacrow.client.console.components.panels.TaskPanel;
import org.datacrow.client.console.wizards.WizardException;
import org.datacrow.core.DcConfig;
import org.datacrow.core.DcRepository;
import org.datacrow.core.clients.IItemImporterClient;
import org.datacrow.core.log.DcLogManager;
import org.datacrow.core.log.DcLogger;
import org.datacrow.core.migration.itemimport.ItemImporter;
import org.datacrow.core.migration.itemimport.ItemImporters;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.ValidationException;
import org.datacrow.core.resources.DcResources;
import org.datacrow.core.server.Connector;
import org.datacrow.core.settings.DcSettings;

public class ItemImporterTaskPanel extends ItemImporterWizardPanel implements IItemImporterClient  {

	private transient static final DcLogger logger = DcLogManager.getInstance().getLogger(ItemImporterTaskPanel.class.getName());
	
	private final TaskPanel tp = new TaskPanel(TaskPanel._SINGLE_PROGRESSBAR);
	
    private final ItemImporterWizard wizard;
    
    private ItemImporter importer;
	
    private int created = 0;
	private int updated = 0;
    
    
    public ItemImporterTaskPanel(ItemImporterWizard wizard) {
        this.wizard = wizard;
        build();
    }
    
	@Override
    public Object apply() throws WizardException {
        return wizard.getDefinition();
    }

    @Override
    public void cleanup() {
    	if (importer != null) importer.cancel();
    	importer = null;
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("msgImportProcess");
    }
    
    @Override
    public void onActivation() {
    	if (wizard.getDefinition() != null) {
    		ItemImporterDefinition definition = wizard.getDefinition();
    		
    		try {
    			this.importer = ItemImporters.getInstance().getImporter(definition.getType(), definition.getModule());
    			
    			for (String field : definition.getMappings().getSourceFields()) 
    				this.importer.addMapping(field, definition.getMappings().getTarget(field));
    			
        		start();
    		
    		} catch (Exception e) {
    			logger.error(e, e);
    		}
    		
    	}
	}

    @Override
	public void onDeactivation() {
		cancel();
	}

    private void start() {
    	importer.setClient(this);
    	
    	try { 
    	    created = 0;
    	    updated = 0;
    	    
    	    if (importer.getFile() == null)
    	        importer.setFile(wizard.getDefinition().getFile());
    	    
    	    importer.start();
    	    
    	} catch (Exception e ) {
    	    notify(e.getMessage());
    	    logger.error(e, e);
    	}
    }
    
    private void build() {
        setLayout(Layout.getGBL());
        add(tp,  Layout.getGBC( 0, 01, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));
    }
    
    private void cancel() {
        if (importer != null) importer.cancel();
        notifyTaskCompleted(true, null);
    }    
    
    @Override
    public void notify(String msg) {
        if (tp != null)
            tp.addMessage(msg);
    }

    @Override
    public void notifyTaskStarted(int count) {
        tp.clear();
        tp.initializeTask(count);
    }

    @Override
    public void notifyTaskCompleted(boolean success, String ID) {
        notify("\n");
        notify(DcResources.getText("msgItemsCreated", String.valueOf(created)));
        notify(DcResources.getText("msgItemsUpdated", String.valueOf(updated)));
        notify(DcResources.getText("msgItemsImported", String.valueOf(updated + created)));
        notify("\n");
        notify(DcResources.getText("msgImportFinished"));
        
        if (tp != null) tp.updateProgressToFinished();
    }

    @Override
    public void notifyProcessed(DcObject item) {
        // Always use this one. If an ID is defined we will use it.
        
        Connector conn = DcConfig.getInstance().getConnector();
        
        String ID = item.getID();
        
        DcObject other = null;
        DcObject otherChild = null;
        
        if (DcSettings.getBoolean(DcRepository.Settings.stImportMatchAndMerge)) {
            other = conn.getItem(item.getModule().getIndex(), ID);
            
            // Check if the item exists and if so, update the item with the found values. Else just create a new item.
            // This is to make sure the order in which the files are processed (first software, then categories)
            // is of no importance (!).
            other = other == null ? conn.getItemByUniqueFields(item) : other;
            other = other == null ? conn.getItemByKeyword(item.getModule().getIndex(), item.toString()) : other;
        } else {
        	// we work with IDs - to avoid violations;
        	other = conn.getItem(item.getModule().getIndex(), ID);
        }

        try {
            if (other != null) {
                updated++;
                other.copy(item, true, false);
                
                if (item.getCurrentChildren() != null) {
                    for (DcObject child : item.getCurrentChildren()) {
                        otherChild = conn.getItem(child.getModule().getIndex(), ID);
                        otherChild = otherChild == null ? conn.getItemByUniqueFields(child) : otherChild;
                        otherChild = otherChild == null ? conn.getItemByKeyword(child.getModule().getIndex(), child.toString()) : otherChild;
                        
                        if (otherChild != null) {
                            otherChild.copy(child, true, false);
                        } else {
                            other.addChild(child.clone());
                        }
                    }  
                }
                conn.saveItem(other);
            } else {
                created++;
                item.setUpdateGUI(false);
                item.setValidate(false);
                item.setIDs();
                
                for (DcObject child : item.getChildren()) {
                	child.setValidate(false);
                    child.setIDs();
                }
                
                conn.saveItem(item);
            }
            
        } catch (ValidationException ve) {
            // will not occur as validation has been disabled.
        	notify(ve.getMessage());
        }
        
        tp.updateProgressTask();
        notify(DcResources.getText("msgImportedX", item.toString()));
        item.cleanup();
    }

    @Override
    public void notifyWarning(String msg) {}

    @Override
    public void notifyError(Throwable e) {}

    @Override
    public void notifyProcessed() {}
    
    @Override
    public boolean askQuestion(String msg) {
        return GUI.getInstance().displayQuestion(msg);
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
