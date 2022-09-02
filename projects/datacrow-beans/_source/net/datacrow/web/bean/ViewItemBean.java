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
import javax.faces.bean.SessionScoped;

import net.datacrow.core.DcConfig;
import net.datacrow.core.server.Connector;
import net.datacrow.web.model.Item;
import net.datacrow.web.util.WebUtilities;

import org.apache.log4j.Level;

@ManagedBean
@SessionScoped
public class ViewItemBean extends ItemBean {

    private static final long serialVersionUID = 1L;

    @Override
    public void setItem(Item item) {
        this.item = item;
        
        Connector conn = DcConfig.getInstance().getConnector();
        
        this.item.setValues(conn.getItem(item.getModuleIdx(), item.getID()));
        this.item.loadAllOtherItems();
        
        // add the item to the bread crumb
        try {
            ViewItemBreadCrumbBean viewItemBreadCrumbBean = (ViewItemBreadCrumbBean) WebUtilities.getBean("viewItemBreadCrumbBean");
            viewItemBreadCrumbBean.addItem(item);
        } catch (Exception e) {
            WebUtilities.log(Level.ERROR_INT, "Could not find / instantiate the Bread Crumb Bean", e);
        }
    }
}
