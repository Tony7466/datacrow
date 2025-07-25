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

package org.datacrow.client.console.wizards.module;

import java.util.ArrayList;
import java.util.List;

import org.datacrow.client.console.GUI;
import org.datacrow.client.console.wizards.IWizardPanel;
import org.datacrow.client.console.wizards.Wizard;
import org.datacrow.client.console.wizards.WizardException;
import org.datacrow.core.DcRepository;
import org.datacrow.core.modules.DcModule;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.modules.ModuleJar;
import org.datacrow.core.modules.xml.XmlField;
import org.datacrow.core.modules.xml.XmlModule;
import org.datacrow.core.resources.DcResources;
import org.datacrow.core.settings.DcSettings;

public class AlterModuleWizard extends Wizard {
    
    public AlterModuleWizard() {
        super();
        
        setTitle(DcResources.getText("lblModuleAlterWizard"));
        setHelpIndex("dc.modules.alter");

        setSize(DcSettings.getDimension(DcRepository.Settings.stModuleWizardFormSize));
        setCenteredLocation();
    }
    
    @Override
    protected void initialize() {
    }
    
    @Override
    protected List<IWizardPanel> getWizardPanels() {
        List<IWizardPanel> panels = new ArrayList<IWizardPanel>();
        panels.add(new PanelSelectModule(this));
        panels.add(new PanelBasicInfo(this, true));
        panels.add(new PanelFields(this, true));
        return panels;
    }

    @Override
    protected void saveSettings() {
        DcSettings.set(DcRepository.Settings.stModuleWizardFormSize, getSize());
    }

    @Override
    public void finish() throws WizardException {
        XmlModule module = (XmlModule) getCurrent().apply();

        try {
            ModuleJar mj = new ModuleJar(module);
            mj.save();
            
            DcModule m;
            for (XmlField field : module.getFields()) {
                if (field.getModuleReference() != module.getIndex() && field.getModuleReference() != 0) {
                    m = DcModules.get(field.getModuleReference()) == null ? 
                                 DcModules.get(field.getModuleReference() + module.getIndex()) : 
                                 DcModules.get(field.getModuleReference());
                    
                    if (m != null && m.getXmlModule() != null)
                        new ModuleJar(m.getXmlModule()).save();
                }
            }

            close();
            
        } catch (Exception e) {
            throw new WizardException(DcResources.getText("msgCouldNotWriteModuleFile", e.getMessage()));
        }
    }

    @Override
    public void next() {
        try {
            XmlModule module = (XmlModule) getCurrent().apply();

            if (module == null)
                return;
            
            current += 1;
            if (current <= getStepCount()) {
                for (int i = 0; i < getStepCount(); i++) {
                    ModuleWizardPanel panel = (ModuleWizardPanel) getWizardPanel(i);
                    panel.setModule(module);
                    panel.setVisible(i == current);
                }
            } else {
                current -= 1;
            }

            applyPanel();
        } catch (WizardException wzexp) {
            if (wzexp.getMessage().length() > 1)
            	GUI.getInstance().displayWarningMessage(wzexp.getMessage());
        }
    }

    @Override
    public void close() {
        if (!isCancelled() && !isRestarted())
            new RestartDataCrowDialog(this);
        
        super.close();
    }

    @Override
    protected String getWizardName() {
        return DcResources.getText("msgModuleWizard",
                                   new String[] {String.valueOf(current + 1), String.valueOf(getStepCount())});
    }

    @Override
    protected void restart() {
        try {
            finish();
            saveSettings();
            CreateModuleWizard wizard = new CreateModuleWizard();
            wizard.setVisible(true);
        } catch (WizardException exp) {
        	GUI.getInstance().displayWarningMessage(exp.getMessage());
        }
    }
}
