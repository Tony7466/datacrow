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

import javax.el.ELContext;
import javax.faces.context.FacesContext;

import net.datacrow.core.DcConfig;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.server.Connector;
import net.datacrow.web.DcBean;
import net.datacrow.web.model.Item;
import net.datacrow.web.model.Reference;
import net.datacrow.web.util.WebUtilities;

import org.apache.log4j.Level;

public abstract class ItemBean extends DcBean {

    private static final long serialVersionUID = 1L;
    
    protected Item item;
    
    public abstract void setItem(Item item);
    
    public void setReference(Reference ref) {
        Connector conn = DcConfig.getInstance().getConnector();
        DcObject dco = conn.getItem(ref.getModule(), ref.getId());
        
        if (dco != null) {
            setItem(new Item(dco));
        } else {
            WebUtilities.log(Level.WARN_INT, "Could not find item with ID " + ref.getId() + " of module " + ref.getModule());
        }
    }
    
    public Item getItem() {
        return item;
    }
    
    protected ItemsBean getItemsBean() {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        return (ItemsBean) elContext.getELResolver().getValue(elContext, null, "itemsBean");
    }
}
