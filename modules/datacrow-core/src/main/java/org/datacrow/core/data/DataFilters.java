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

package org.datacrow.core.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.datacrow.core.DcConfig;
import org.datacrow.core.DcRepository;
import org.datacrow.core.DcRepository.ValueTypes;
import org.datacrow.core.console.UIComponents;
import org.datacrow.core.log.DcLogManager;
import org.datacrow.core.log.DcLogger;
import org.datacrow.core.modules.DcModule;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcField;
import org.datacrow.core.resources.DcResources;
import org.datacrow.core.utilities.CoreUtilities;
import org.datacrow.core.utilities.definitions.DcFieldDefinition;


/**
 * Holder of all saved data filters
 * 
 * @author Robert Jan van der Waals
 */
public class DataFilters {
    
    private transient static final DcLogger logger = DcLogManager.getInstance().getLogger(DataFilters.class.getName());
    
    private static final Map<Integer, Collection<DataFilter>> filters = 
        new HashMap<Integer, Collection<DataFilter>>(); 
    private static final Map<Integer, DataFilter> activeFilters = 
        new HashMap<Integer, DataFilter>();
    
    private static final File file = new File(DcConfig.getInstance().getApplicationSettingsDir(), "filters.xml");

    /**
     * Creates a new instance.
     */
    private DataFilters() {}
    
    public static DataFilter createSearchAllFilter(int moduleIdx, String searchString) {
    	return createSearchAllFilter(moduleIdx, -1, searchString);
    }
    
    public static DataFilter createSearchAllFilter(int moduleIdx, int fieldIdx, String searchString) {
        if (CoreUtilities.isEmpty(searchString)) 
            return null;
        
        DcModule module = DcModules.get(moduleIdx);
        
        boolean valueNumeric = true;
        try {
            Double.parseDouble(searchString);
        } catch (NumberFormatException nfe) {
            valueNumeric = false;
        }
        
        DataFilter df = DataFilters.getDefaultDataFilter(moduleIdx);
        df = df == null ? new DataFilter(moduleIdx) : df;
        
        DcField field;
        boolean fieldNumeric;
        boolean fieldBoolean;
        Object value;
        for (DcFieldDefinition fd : DcModules.get(moduleIdx).getFieldDefinitions().getDefinitions()) {
            
        	// skip the field if is not matching the specified field
        	if (fd.getIndex() == fieldIdx || fieldIdx == -1) {
        	
	            field = module.getField(fd.getIndex());
	            
	            if (field == null) {
	            	logger.debug("Field does not exists, skipping for filter all, index: " + fd.getIndex());
	            	continue;
	            }
	            
	            int vt = field.getValueType();
	            
	            fieldNumeric = 
	                    vt == DcRepository.ValueTypes._BIGINTEGER ||
	                    vt == DcRepository.ValueTypes._DOUBLE ||
	                    vt == DcRepository.ValueTypes._LONG ||
	                    field.getFieldType() == UIComponents._NUMBERFIELD;
	            
	            fieldBoolean = vt == DcRepository.ValueTypes._BOOLEAN;
	            
	            if (!field.isEnabled() || // field has to be enabled according to the security and settings
	                !field.isSearchable() || // field has to be search-able
	                (fieldNumeric && !valueNumeric) || // can't do a contain on a numeric value
	                (field.isUiOnly() && field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION)) 
	                    continue;
	            
	            switch (vt) {
	            	case ValueTypes._DOUBLE:
	            		value = Double.valueOf(searchString);
	            		break;
	            	case ValueTypes._BOOLEAN:
	            		if (searchString.equalsIgnoreCase("true") ||
	            			searchString.equalsIgnoreCase("1") ||
	            			searchString.equalsIgnoreCase(DcResources.getText("lblYes")))
	            			value = Boolean.TRUE;
	            		else
	            			continue;
	            		break;
	            	default:
	            		value = searchString;
	            		break;
	            }
	            
	            DataFilterEntry dfe = new DataFilterEntry(DataFilterEntry._OR, 
	                                                      fd.getModule(), 
	                                                      fd.getIndex(), 
	                                                      fieldNumeric || fieldBoolean ? Operator.EQUAL_TO : Operator.CONTAINSVALUE,
	                                                      value);
	            df.addEntry(dfe);
        	}
        }

        return df;
    }
    
    /**
     * Loads the settings file
     */
    public static void load() {
        
        if (!file.exists())
            return;
        
        try {
            filters.clear();
            byte[] b = CoreUtilities.readFile(file);
            String xml = new String(b, "UTF-8");
            
            while (xml.indexOf("<FILTER>") > -1) {
                String part = xml.substring(xml.indexOf("<FILTER>"), xml.indexOf("</FILTER>") + 9);
                DataFilter df = new DataFilter(part);
                
                Collection<DataFilter> c = filters.get(df.getModule());
                c = c == null ? new ArrayList<DataFilter>() : c;
                c.add(df);
                filters.put(df.getModule(), c);
                
                xml = xml.substring(xml.indexOf("</FILTER>") + 9, xml.length());
            }
        } catch (Exception exp) {
            logger.error("An error occurred while loading filters from " + file, exp);
        }
    }
    
    /**
     * Stores the currently applied filter.
     * @param module
     * @param df
     */
    public static void setCurrent(int module, DataFilter df) {
    	activeFilters.put(module, df);
    }
    
    /**
     * Retrieves the default filter. This filter is an empty filter with the default ordering set.
     * @param module
     * @return
     */
    public static DataFilter getDefaultDataFilter(int module) {
        DataFilter filter = new DataFilter(module);
        
        DcModule m = DcModules.get(module);
        int[] fields = m.getSettings().getIntArray(DcRepository.ModuleSettings.stSearchOrder);
        Collection<DcField> order = new ArrayList<DcField>();
        for (int fieldIdx : fields) {
        	if (m.getField(fieldIdx) != null)
        		order.add(m.getField(fieldIdx));
        }
        
        filter.setOrder(order);
        return filter;
    }
    
    /**
     * Retrieves the currently applied filter for the specified module.
     * @param module
     */
    public static DataFilter getCurrent(int module) {
    	DataFilter df = activeFilters.get(module);
    	return df == null ? getDefaultDataFilter(module) : df;
    }
    
    public static boolean isFilterActive(int module) {
        return activeFilters.get(module) != null;
    }
    
    /**
     * Save all filters to file for future use.
     */
    public static void save() {
        String xml = "<FILTERS>\n";

        for (Collection<DataFilter> c : filters.values()) {
            for (DataFilter df : c)
                xml += "\n" + df.toStorageString();
        }
        
        xml += "</FILTERS>";
        
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(xml.getBytes("UTF-8"));
            fos.close();
        } catch (Exception exp) {
            logger.error("An error occurred while saving filters to " + file, exp);
        }         
    }
    
    /**
     * Delete a filter.
     * @param df
     */
    public static void delete(DataFilter df) {
        Collection<DataFilter> c = get(df.getModule());
        c.remove(df);
    }
    
    /**
     * Gets all saved filters for the specified module.
     * @param module
     */
    public static Collection<DataFilter> get(int module) {
        Collection<DataFilter> c = filters.get(module);
        return c != null ? c : new ArrayList<DataFilter>();
    }

    /**
     * Adds a filter. This filter will be saved to file.
     * @param df
     */
    public static void add(DataFilter df) {
        Collection<DataFilter> c = filters.get(df.getModule());
        c = c == null ? new ArrayList<DataFilter>() : c;
        c.remove(df);
        c.add(df);
        filters.put(df.getModule(), c);
    }
}
