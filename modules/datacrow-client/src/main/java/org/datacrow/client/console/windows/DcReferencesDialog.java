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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import org.datacrow.client.console.ComponentFactory;
import org.datacrow.client.console.GUI;
import org.datacrow.client.console.Layout;
import org.datacrow.client.console.components.DcButton;
import org.datacrow.client.console.components.DcReferencesField;
import org.datacrow.client.console.components.DcShortTextField;
import org.datacrow.client.console.components.renderers.SimpleValueTableCellRenderer;
import org.datacrow.client.console.components.tables.DcTable;
import org.datacrow.client.console.windows.itemforms.IItemFormListener;
import org.datacrow.client.console.windows.itemforms.ItemForm;
import org.datacrow.core.DcConfig;
import org.datacrow.core.DcRepository;
import org.datacrow.core.IconLibrary;
import org.datacrow.core.data.DataFilter;
import org.datacrow.core.modules.DcModule;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.modules.MappingModule;
import org.datacrow.core.objects.DcMapping;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.DcSimpleValue;
import org.datacrow.core.resources.DcResources;
import org.datacrow.core.server.Connector;
import org.datacrow.core.settings.DcSettings;

public class DcReferencesDialog extends DcDialog implements ActionListener, KeyListener, IItemFormListener {
    
    private DcTable tblSelectedItems;
    private DcTable tblAvailableItems;
    private Collection<DcSimpleValue> availableItems = new ArrayList<DcSimpleValue>();
    
    private final JButton btCreate = ComponentFactory.getIconButton(IconLibrary._icoOpenNew);
    
    private MappingModule mappingModule;
    
    private boolean saved = false;
    
    private final DcReferencesField fld;
    
    public DcReferencesDialog(
    		Collection<DcObject> currentItems, 
    		MappingModule mappingModule,
    		DcReferencesField fld) {
    	
        this.mappingModule = mappingModule;
        this.fld = fld;
        Collection<DcObject> current = currentItems == null ? new ArrayList<DcObject>() : currentItems;
        
        setTitle(DcModules.get(mappingModule.getReferencedModIdx()).getObjectNamePlural());
        buildDialog();
        
        setHelpIndex("dc.items.itemform_multiref");
        
        DcSimpleValue sv;
        DcObject reference;
        DcMapping mapping;
        Collection<DcSimpleValue> selected = new ArrayList<DcSimpleValue>();
        for (DcObject dco : current) {
            mapping = (DcMapping) dco;
            reference = mapping.getReferencedObject();
            if (reference != null) {
                sv = new DcSimpleValue(reference.getID(), reference.toString(), reference.getIcon());
                
                selected.add(sv);
                tblSelectedItems.addRow(new DcSimpleValue[] {sv});
            }
        }
        
        Connector connector = DcConfig.getInstance().getConnector();
        
        DcModule m = DcModules.get(mappingModule.getReferencedModIdx());
        List<DcObject> all =  connector.getItems(new DataFilter(m.getIndex()), m.getMinimalFields(null));
        
        for (DcObject dco : all) {
            sv = new DcSimpleValue(dco.getID(), dco.toString(), dco.getIcon());
            
            if (!selected.contains(sv)) {
            	availableItems.add(sv);
            	tblAvailableItems.addRow(new DcSimpleValue[] {sv});
            }
        }
        
        pack();
        
        setModal(false);
        
        setSize(DcSettings.getDimension(DcRepository.Settings.stReferencesDialogSize));
        setCenteredLocation();
    }
    
    private Collection<DcSimpleValue> getValues(DcTable table) {
        Collection<DcSimpleValue> values = new ArrayList<DcSimpleValue>();
        for (int row = 0; row < table.getRowCount(); row++)
            values.add((DcSimpleValue) table.getValueAt(row, 0));
        
        return values;
    }
    
    public Collection<DcObject> getDcObjects() {
        Collection<DcObject> items = new ArrayList<DcObject>();
        
        DcMapping mapping;
        for (DcSimpleValue sv : getValues(tblSelectedItems)) {
            mapping = (DcMapping) mappingModule.getItem();
            mapping.setValue(DcMapping._B_REFERENCED_ID, sv.getID());
            items.add(mapping);
        }
        return items;
    }

    public void clear() {
        if (tblAvailableItems != null)
            tblAvailableItems.clear();
        
        if (tblSelectedItems != null)
            tblSelectedItems.clear();
        
        tblSelectedItems = null;
        tblAvailableItems = null;

        mappingModule = null;
    }

    public boolean isSaved() {
        return saved;
    }
    
    private void save() {
        saved = true;
        fld.setValue(getDcObjects());
        close();
    }
    
    private void move(boolean rightToLeft) {
        if (rightToLeft) {
            int[] rows = tblSelectedItems.getSelectedRows();
            for (int i = rows.length - 1; i < rows.length && i > -1; i--) {
                DcSimpleValue sv = (DcSimpleValue) tblSelectedItems.getValueAt(rows[i], 0);
                tblSelectedItems.getDcModel().removeRow(rows[i]);
                tblAvailableItems.addRow(new Object[] {sv});
                availableItems.add(sv);
            }
            tblSelectedItems.clearSelection();
        } else {
            int[] rows = tblAvailableItems.getSelectedRows();
            for (int i = rows.length - 1; i < rows.length && i > -1; i--) {
                DcSimpleValue sv = (DcSimpleValue) tblAvailableItems.getValueAt(rows[i], 0);
                tblAvailableItems.getDcModel().removeRow(rows[i]);
                tblSelectedItems.addRow(new Object[] {sv});
                availableItems.remove(sv);
            }
            tblAvailableItems.clearSelection();
        }        
    }
    
    @Override
    public void notifyItemSaved(DcObject dco) {
    	DcSimpleValue sv = new DcSimpleValue(dco.getID(), dco.toString(), dco.getIcon());
    	availableItems.add(sv);
    	tblSelectedItems.addRow(new Object[] {sv});
    }    
    
    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stReferencesDialogSize, getSize());
        clear();
        setVisible(false);
    }
    
    private void buildDialog() {
        getContentPane().setLayout(Layout.getGBL());
        
        JPanel panelbuttons = new JPanel();
        panelbuttons.setLayout(Layout.getGBL());
        
        JButton btLeft = new DcButton();
        JButton btRight = new DcButton();
        btLeft.setText("<");
        btRight.setText(">");
        
        btLeft.setActionCommand("toleft");
        btRight.setActionCommand("toright");
        btLeft.addActionListener(this);
        btRight.addActionListener(this);
        
        panelbuttons.add(btRight, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets( 0, 0, 0, 0), 0, 0));
        panelbuttons.add(btLeft,  Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets( 0, 0, 0, 0), 0, 0));

        JTextField txtFilter = ComponentFactory.getShortTextField(255);
        txtFilter.addKeyListener(this);

        tblAvailableItems = new DcTable(true, false);
        tblAvailableItems.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblAvailableItems.addMouseListener(new ListMouseListener(ListMouseListener._RIGHT));
        
        tblAvailableItems.setColumnCount(1);
        TableColumn cSimpleVal = tblAvailableItems.getColumnModel().getColumn(0);
        cSimpleVal.setCellRenderer(SimpleValueTableCellRenderer.getInstance());
        cSimpleVal.setHeaderValue(DcResources.getText("lblAvailable"));
        
        tblSelectedItems = new DcTable(true, false);
        tblSelectedItems.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblSelectedItems.addMouseListener(new ListMouseListener(ListMouseListener._LEFT));

        tblSelectedItems.setColumnCount(1);
        cSimpleVal = tblSelectedItems.getColumnModel().getColumn(0);
        cSimpleVal.setCellRenderer(SimpleValueTableCellRenderer.getInstance());
        cSimpleVal.setHeaderValue(DcResources.getText("lblSelected"));

        JScrollPane scrollerLeft = new JScrollPane(tblAvailableItems);
        scrollerLeft.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollerLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JScrollPane scrollerRight = new JScrollPane(tblSelectedItems);
        scrollerRight.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollerRight.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JButton buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        
        // action panel
        
        JPanel panelActions = new JPanel();
        panelActions.add(buttonSave);
        panelActions.add(buttonClose);
        
        buttonSave.addActionListener(this);
        buttonSave.setActionCommand("save");
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");

        // filter panel
        
        btCreate.addActionListener(this);
        btCreate.setActionCommand("create");
        
        JPanel panelFilter = new JPanel();
        panelFilter.setLayout(Layout.getGBL());
        panelFilter.add(txtFilter, Layout.getGBC( 0, 0, 1, 1, 100.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 0, 0, 0), 0, 0));        
        panelFilter.add(btCreate, Layout.getGBC( 1, 0, 1, 1, 0.0, 0.0
                ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                 new Insets( 0, 0, 0, 0), 0, 0));      
        
        // main panel
        
        getContentPane().add(panelFilter,     Layout.getGBC( 0, 0, 3, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 0, 5), 0, 0));
        getContentPane().add(scrollerLeft,  Layout.getGBC( 0, 1, 1, 1, 40.0, 40.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 5, 0, 5), 0, 0));
        getContentPane().add(panelbuttons,  Layout.getGBC( 1, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.CENTER, GridBagConstraints.NONE,
                 new Insets( 0, 0, 0, 0), 0, 0));
        getContentPane().add(scrollerRight, Layout.getGBC( 2, 1, 1, 1, 40.0, 40.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 5, 0, 5), 0, 0));
        getContentPane().add(panelActions,  Layout.getGBC( 0, 2, 3, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 0), 0, 0));
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close"))
            close();
        else if (ae.getActionCommand().equals("save"))
            save();
        else if (ae.getActionCommand().equals("toright"))
            move(false);        
        else if (ae.getActionCommand().equals("toleft"))
            move(true);
        else if (ae.getActionCommand().equals("create"))
            create();
    }
    
    private void create() {
//    	this.setDirectVisible(false);
    	
        DcObject dco = DcModules.get(mappingModule.getReferencedModIdx()).getItem();
        ItemForm itemForm = new ItemForm(false, false, dco, true);
        
        GUI.getInstance().setRootFrame(itemForm);
        
        itemForm.setListener(this);
        itemForm.setVisible(true);
        
        itemForm.toFront();
    }    
    
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        DcShortTextField txtFilter = (DcShortTextField) e.getSource();
        String filter = txtFilter.getText();
        
        if (filter.trim().length() == 0) {
            tblAvailableItems.clear();
            tblAvailableItems.setRowCount(availableItems.size());
            int row = 0;
            for (DcSimpleValue sv : availableItems)
                tblAvailableItems.setValueAt(sv, row++, 0);
        } else {
            Collection<DcSimpleValue> filtered = new ArrayList<DcSimpleValue>();
            for (DcSimpleValue sv : availableItems) {
                if (sv.getName().toLowerCase().contains(filter.toLowerCase()))
                    filtered.add(sv);
            }
        
            tblAvailableItems.clear();
            tblAvailableItems.setRowCount(filtered.size());
            int row = 0;
            for (DcSimpleValue sv : filtered)
                tblAvailableItems.setValueAt(sv, row++, 0);
        }
    }
    
    private class ListMouseListener implements MouseListener {
        
        public static final int _LEFT = 0;
        public static final int _RIGHT = 1;
        
        int direction;
        
        public ListMouseListener(int direction) {
            this.direction = direction;
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() == 2) {
                if (direction == _LEFT) {
                    move(true);
                } else {
                    move(false);
                }
            }  
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseClicked(MouseEvent e) {}
    }
}
