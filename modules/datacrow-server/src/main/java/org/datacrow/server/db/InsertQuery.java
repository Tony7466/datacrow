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

package org.datacrow.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.datacrow.core.DcRepository;
import org.datacrow.core.log.DcLogManager;
import org.datacrow.core.log.DcLogger;
import org.datacrow.core.objects.DcField;
import org.datacrow.core.objects.DcMapping;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.security.SecuredUser;

public class InsertQuery extends Query {
    
    private transient static final DcLogger logger = DcLogManager.getInstance().getLogger(InsertQuery.class.getName());
    
    private final DcObject dco;
    
    public InsertQuery(SecuredUser su, DcObject dco) {
        super(su, dco.getModule().getIndex());
        this.dco = dco;
        this.dco.setIDs();
    }

    @Override
    @SuppressWarnings("resource")
    public List<DcObject> run() {
        Collection<Object> values = new ArrayList<Object>();
        StringBuffer columns = new StringBuffer();

        // create non existing references
        createReferences(dco);
	       
        Collection<DcMapping> references = new ArrayList<DcMapping>();

        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseManager.getInstance().getConnection(getUser());
            stmt = conn.createStatement();
        
            for (DcField field : dco.getFields()) {
                if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                    @SuppressWarnings("unchecked")
                    Collection<DcMapping> c = (Collection<DcMapping>) dco.getValue(field.getIndex());
                    if (c != null) references.addAll(c);                
                    
                } else if (!field.isUiOnly()) {
                    if (columns.length() > 0)
                        columns.append(", ");
    
                    values.add(getQueryValue(dco, field.getIndex()));
                    columns.append(field.getDatabaseFieldName());
                }
            }
            
            String sqlPart = "";
            for (int i = 0; i < values.size(); i++)
                sqlPart += (sqlPart.length() > 0 ? ", ?" : "?"); 
            
            String sql = "INSERT INTO " + dco.getTableName() + " (" + columns + ") \r\n" + "VALUES (" + sqlPart + ");";
            
            ps = conn.prepareStatement(sql);
            setValues(ps, values);
            ps.execute();
            
            saveReferences(references, dco.getID());
            
            for (DcField f : dco.getFields()) {
                if (f.getValueType() == DcRepository.ValueTypes._ICON) {
                    saveIcon((String) dco.getValue(f.getIndex()), f, dco.getID());
                }
            }

            InsertQuery insertQuery;
            if (dco.getCurrentChildren() != null) {
                for (DcObject child : dco.getCurrentChildren()) {
                    try {
                        child.setValue(child.getParentReferenceFieldIndex(), dco.getID());
                        insertQuery = new InsertQuery(getUser(), child);
                        insertQuery.run();
                    } catch (Exception e) {
                        logger.error("An error occured while inserting the following child object: " + child, e);
                    }                         
                }
            }
            
            setSuccess(true);
            
        } catch (SQLException e) {
            setSuccess(false);
            logger.error("An error occured while running the query", e);
        }
        
        try {
            if (ps != null) ps.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            logger.error("Error while closing connection", e);
        }
        
        return null;
    }
}
