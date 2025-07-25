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

package org.datacrow.core.utilities.definitions;

import org.datacrow.core.modules.DcModules;

public class WebOverviewFieldDefinition extends Definition {

	private static final long serialVersionUID = 1L;

	private final int moduleIdx;
    private final int fieldIdx;
    private boolean enabled;
    
    public WebOverviewFieldDefinition(int moduleIdx, int fieldIdx, boolean enabled) {
        super();
        
        this.moduleIdx = moduleIdx;
        this.fieldIdx = fieldIdx;
        this.enabled = enabled;
    }
    
    public int getFieldIdx() {
        return fieldIdx;
    }
    
    public int getModuleIdx() {
        return moduleIdx;
    }
    
    public boolean isEnabled() {
        return enabled;
    }     
    
    public String getLabelKey() {
        return DcModules.get(moduleIdx).getField(fieldIdx).getResourceKey();
    }

    @Override
    public String toSettingValue() {
        return String.valueOf(fieldIdx) + "/&/" + enabled;
    }    
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof WebOverviewFieldDefinition) {
            WebOverviewFieldDefinition def = (WebOverviewFieldDefinition) o;
            
            return  def.getFieldIdx() == getFieldIdx() &&
            		def.getModuleIdx() == getModuleIdx();
        }
        return false;
    }

	@Override
	public int hashCode() {
		return getFieldIdx() + (getModuleIdx() * 10000);
	}
}
