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

package org.datacrow.client.console.windows.settings;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.datacrow.client.console.ComponentFactory;
import org.datacrow.client.console.GUI;
import org.datacrow.client.console.Layout;
import org.datacrow.client.console.components.DcColorSelector;
import org.datacrow.client.console.components.fileselection.FieldSelectionPanel;
import org.datacrow.client.console.windows.DcDialog;
import org.datacrow.core.DcRepository;
import org.datacrow.core.IconLibrary;
import org.datacrow.core.modules.DcModule;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcField;
import org.datacrow.core.resources.DcResources;
import org.datacrow.core.settings.DcSettings;

public class TableViewSettingsDialog extends DcDialog implements ActionListener {
    
	private FieldSelectionPanel fspParent = null;
    private FieldSelectionPanel fspChild = null;
    
    public TableViewSettingsDialog() {
        super();
        
        setIconImage(IconLibrary._icoViewSettings.getImage());
        setTitle(DcResources.getText("lblViewSettings"));
        setHelpIndex("dc.settings.tableview");

        DcModule module = DcModules.getCurrent();
        
        fspParent = new FieldSelectionPanel(module, true, true, true);
        fspParent.setSelectedFields((int[]) module.getSetting(DcRepository.ModuleSettings.stTableColumnOrder));
        if (module.getChild() != null) {
            fspChild = new FieldSelectionPanel(module.getChild(), true, true, true);
            fspChild.setSelectedFields((int[]) module.getChild().getSetting(DcRepository.ModuleSettings.stTableColumnOrder));
        }
        
        build();
        pack();
        
        setSize(module.getSettings().getDimension(DcRepository.ModuleSettings.stTableViewSettingsDialogSize));
        setCenteredLocation();
    }

    private void save(DcModule module, List<DcField> selected) {
        int[] fields = new int[selected.size()];
        int i = 0;
        for (DcField fld : selected)
            fields[i++] = fld.getIndex(); 

        module.setSetting(DcRepository.ModuleSettings.stTableColumnOrder, fields);
        
        GUI gui = GUI.getInstance();
        if (module.hasSearchView())
        	gui.getSearchView(module.getIndex()).applySettings();

        if (module.hasInsertView())
        	gui.getInsertView(module.getIndex()).applySettings();
    }
    
    @Override
    public void close() {
        DcModules.getCurrent().setSetting(DcRepository.ModuleSettings.stTableViewSettingsDialogSize, getSize());
        
        if (fspParent != null) {
            fspParent.clear();
            fspParent = null;
        }
        
        if (fspChild != null) {
            fspChild.clear();
            fspChild = null;
        }        

        super.close();
    }

    private void build() {
        setLayout(Layout.getGBL());
        
        //**********************************************************
        //Tabbed pane
        //**********************************************************
        JTabbedPane tp = ComponentFactory.getTabbedPane();
        
        DcColorSelector csOdd = ComponentFactory.getColorSelector(DcRepository.Settings.stOddRowColor);
        DcColorSelector csEven = ComponentFactory.getColorSelector(DcRepository.Settings.stEvenRowColor);
        DcColorSelector scHeader = ComponentFactory.getColorSelector(DcRepository.Settings.stTableHeaderColor);
        
        csOdd.setValue(DcSettings.getColor(DcRepository.Settings.stOddRowColor));
        csEven.setValue(DcSettings.getColor(DcRepository.Settings.stEvenRowColor));
        scHeader.setValue(DcSettings.getColor(DcRepository.Settings.stTableHeaderColor));
        
        tp.addTab(DcResources.getText("lblColumns", DcModules.getCurrent().getLabel()), fspParent);
        
        if (DcModules.getCurrent().getChild() != null)
            tp.addTab(DcResources.getText("lblColumns", DcModules.getCurrent().getChild().getLabel()), fspChild);
        
        tp.addTab(DcResources.getText("lblEvenColor"), csEven);
        tp.addTab(DcResources.getText("lblOddColor"), csOdd);
        tp.addTab(DcResources.getText("lblTableHeaderColor"), scHeader);
        
        //**********************************************************
        //Action Panel
        //**********************************************************
        JButton buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        
        JPanel panelActions = new JPanel();
        panelActions.add(buttonSave);
        panelActions.add(buttonClose);
        
        buttonSave.addActionListener(this);
        buttonClose.addActionListener(this);
        buttonSave.setActionCommand("save");
        buttonClose.setActionCommand("close");

        //**********************************************************
        //Main Panel
        //**********************************************************        
        getContentPane().add(tp, Layout.getGBC(0, 0, 1, 1, 10.0, 10.0
                 ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                  new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelActions, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("save")) {
            save(DcModules.getCurrent(), fspParent.getSelectedFields());
            
            if (DcModules.getCurrent().getChild() != null)
                save(DcModules.getCurrent().getChild(), fspChild.getSelectedFields());
            
            close();
        } else if (e.getActionCommand().equals("close")) {
            close();
        }
    }
}
