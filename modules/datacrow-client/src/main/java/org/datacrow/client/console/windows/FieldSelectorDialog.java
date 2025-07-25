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

package org.datacrow.client.console.windows;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.datacrow.client.console.ComponentFactory;
import org.datacrow.client.console.Layout;
import org.datacrow.client.console.components.DcFieldSelectorField;
import org.datacrow.core.DcRepository;
import org.datacrow.core.log.DcLogManager;
import org.datacrow.core.log.DcLogger;
import org.datacrow.core.modules.DcModule;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.resources.DcResources;

public class FieldSelectorDialog extends DcDialog implements ActionListener { 
    
    private static final FlowLayout layout = new FlowLayout(FlowLayout.RIGHT);
    private transient static final DcLogger logger = DcLogManager.getInstance().getLogger(FieldSelectorDialog.class.getName());    
    
    private final int modIdx;
    private final String settingsKey;
    private final DcFieldSelectorField fs;
    
    public FieldSelectorDialog(JFrame parent, int modIdx, boolean allowPictureFields, String settingsKey) {
        
        super(parent);
        
        this.modIdx = modIdx;
        this.settingsKey = settingsKey;
        this.fs = new DcFieldSelectorField(modIdx, allowPictureFields, true);
        
        setTitle(DcResources.getText("lblOnlineUpdateFieldSettings"));
        
        setModal(true);
        
        build();

        try {
            fs.setSelectedFields((int[]) getModule().getSetting(settingsKey));
        } catch (Exception e) {
            logger.error("Could not set the selected fields (" +  getModule().getSetting(settingsKey) + ")", e);
        }
        
        setSize(getModule().getSettings().getDimension(DcRepository.ModuleSettings.stOnlineSearchFieldSettingsDialogSize));
        setCenteredLocation();
    }
    
    private DcModule getModule() {
        return DcModules.get(modIdx);
    }
    
    @Override
    public void close() {
        getModule().getSettings().set(DcRepository.ModuleSettings.stOnlineSearchFieldSettingsDialogSize, getSize());
        super.close();
    }

    private void apply() {
        getModule().setSetting(settingsKey, fs.getSelectedFieldIndices());
        close();
    }    
    
    private void build() {
        getContentPane().setLayout(Layout.getGBL());
        
        
        
        getContentPane().add(fs, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                             new Insets(5, 5, 5, 5), 0, 0));        
        
        JPanel panelActions = new JPanel();
        panelActions.setLayout(layout);

        JButton buttonApply = ComponentFactory.getButton(DcResources.getText("lblApply"));
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        
        buttonApply.addActionListener(this);
        buttonApply.setActionCommand("apply");
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        
        panelActions.add(buttonApply);  
        panelActions.add(buttonClose);
        
        getContentPane().add(panelActions, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                             new Insets(0, 0, 5, 5), 0, 0));

        pack();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close"))
            close();
        else if (ae.getActionCommand().equals("apply"))
            apply();
    }
}

