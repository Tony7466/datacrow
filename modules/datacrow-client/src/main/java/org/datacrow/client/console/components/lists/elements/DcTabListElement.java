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

package org.datacrow.client.console.components.lists.elements;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;

import org.datacrow.client.console.ComponentFactory;
import org.datacrow.client.tabs.Tab;

public class DcTabListElement extends DcListElement {
    
    private static final FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
    private static final Dimension dim = new Dimension(360, 30);
    private static final Dimension dimIcon = new Dimension(30, 30);
    private static final Dimension dimLabel = new Dimension(320, 30);
    
    private final Tab tab;
    
    public DcTabListElement(Tab tab) {
        this.tab = tab;
        
        setPreferredSize(dim);
        setMinimumSize(dim);
        
        build();
    }

    public Tab getTab() {
        return tab;
    }
    
    @Override
    public void build() {
        
        if (tab == null) return;
        
        setLayout(layout);
        JLabel labelField = ComponentFactory.getLabel(tab.getName());
        labelField.setPreferredSize(dimLabel);
        labelField.setText(tab.getName());
        
        JLabel labelIcon = ComponentFactory.getLabel(tab.getIcon().toIcon());
        labelIcon.setPreferredSize(dimIcon);
        
        add(labelIcon);
        add(labelField);
    }

    @Override
    public int hashCode() {
        return tab.getName().hashCode() + (tab.getModule() * 10000);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DcTabListElement) {
            Tab tab2 = ((DcTabListElement) obj).getTab();
            return tab2 != null && tab2.equals(tab);
        }
        return false;
    }
}
