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

import javax.swing.JRadioButton;

import org.datacrow.client.console.wizards.Wizard;
import org.datacrow.core.modules.DcMediaModule;
import org.datacrow.core.modules.DcMediaParentModule;
import org.datacrow.core.modules.DcModule;
import org.datacrow.core.modules.DcParentModule;
import org.datacrow.core.modules.xml.XmlModule;
import org.datacrow.core.resources.DcResources;

public class PanelSelectChildModule extends PanelSelectModule {

    public PanelSelectChildModule(Wizard wizard) {
        super(wizard);
    }

    @Override
    public Object apply() {
        XmlModule child = (XmlModule) super.apply();
        XmlModule parent = getModule();
        if (parent != null) {
            parent.setChildIndex(child.getIndex());
            
            if (  !(parent.getModuleClass().equals(DcMediaParentModule.class) || 
                    parent.getModuleClass().equals(DcParentModule.class))) {
                
                if (parent.getModuleClass().equals(DcMediaModule.class))
                    parent.setModuleClass(DcMediaParentModule.class);
                else 
                    parent.setModuleClass(DcParentModule.class);
            }
        }
        return parent;
    }
    
    @Override
    public void setModule(XmlModule module) {
        super.setModule(module);
        
        JRadioButton component = getRadioButton(module.getIndex());
        component.setVisible(false);
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("msgSelectChildModule");
    }
    
    @Override
    protected boolean isModuleAllowed(DcModule module) {
        return !module.isAbstract() &&
                module.isTopModule() && 
               !module.isParentModule() && !module.isChildModule();
    }
}