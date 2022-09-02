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

package net.datacrow.web.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.resources.DcResources;
import net.datacrow.web.util.WebUtilities;

import org.apache.log4j.Level;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

@ManagedBean
@ViewScoped
public class MainMenuBean {

    private MenuModel model;

    public MainMenuBean() {
        model = new DefaultMenuModel();
        
        ModulesBean modulesBean;
        LoginBean loginBean;
        DcModule module;
        try {
            modulesBean = (ModulesBean) WebUtilities.getBean("modulesBean");
            
            if (modulesBean.getSelectedModuleIdx() == -1) return;
            
            module = DcModules.get(modulesBean.getSelectedModuleIdx());
            
            loginBean = (LoginBean) WebUtilities.getBean("loginBean");
            
            if (loginBean.isEditingAllowed(module)) {
                DefaultSubMenu firstSubmenu = new DefaultSubMenu(DcResources.getText("lblEdit"));
                
                DefaultMenuItem item = new DefaultMenuItem(DcResources.getText("lblNewItem", module.getObjectName()));
                item.setIcon("fa fa-plus-circle");
                item.setCommand("#{editItemBean.add}");
                item.setUpdate(":editItemDetail");
                item.setOncomplete("PF('editItemDetail').show()");                    
                
                firstSubmenu.addElement(item);
                
                model.addElement(firstSubmenu);
            }
            
            
            if (loginBean.isAdmin()) {
                DefaultSubMenu firstSubmenu = new DefaultSubMenu(DcResources.getText("lblSettings"));
                
                //DefaultMenuItem item = new DefaultMenuItem(DcResources.getText("lblOverviewFieldSettings", module.getObjectName()));
                //item.setIcon("fa fa-wrench");
                //item.setUpdate(":editOverviewsettings");
                //item.setOncomplete("PF('editOverviewsettings').show()");                    
                
                //firstSubmenu.addElement(item);
                
                DefaultMenuItem item = new DefaultMenuItem(DcResources.getText("lblItemFormSettings", module.getObjectName()));
                item.setIcon("fa fa-wrench");
                item.setUpdate(":editItemFormsettings");
                item.setOncomplete("PF('editItemFormsettings').show()");                    
                
                firstSubmenu.addElement(item);
                
                model.addElement(firstSubmenu);
            }
            
        } catch (Exception e) {
            WebUtilities.log(Level.ERROR_INT, e);
        }
    }

    public MenuModel getModel() {
        return model;
    }

}
