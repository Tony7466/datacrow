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

package org.datacrow.server.security;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.datacrow.core.DcConfig;
import org.datacrow.core.log.DcLogManager;
import org.datacrow.core.log.DcLogger;
import org.datacrow.core.modules.DcModule;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcField;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.helpers.Permission;
import org.datacrow.core.objects.helpers.User;
import org.datacrow.core.plugin.Plugins;
import org.datacrow.core.plugin.RegisteredPlugin;
import org.datacrow.core.resources.DcResources;
import org.datacrow.core.security.SecuredUser;
import org.datacrow.core.security.SecurityException;
import org.datacrow.core.security.SecurityToken;
import org.datacrow.core.utilities.CoreUtilities;
import org.datacrow.server.data.DataManager;
import org.datacrow.server.db.CreateQuery;
import org.datacrow.server.db.DatabaseManager;
import org.datacrow.server.db.InsertQuery;

/**
 * The security center is the access point for all security related information.
 * Note that the users logged on to the web application are not represented by this class. 
 * 
 * @author Robert Jan van der Waals
 */
public class SecurityCenter {
    
    private transient static final DcLogger logger = DcLogManager.getInstance().getLogger(SecurityCenter.class.getName());
    private static SecurityCenter instance = new SecurityCenter();
    
    // all logged on users
    private final Map<String, SecuredUser> users = new HashMap<String, SecuredUser>();
    private SecuredUser admin;
    
    /**
     * Retrieves the sole instance of this class
     */
    public static synchronized SecurityCenter getInstance() {
        return instance;
    }

    /**
     * Creates a new instance
     */
    private SecurityCenter() {}
    
    public SecuredUser getAdmin() {
    	if (admin == null) {
    		User user = new User();
    		user.setValue(User._A_LOGINNAME, "DC_ADMIN");
    		user.setIDs();
    		
    		admin = new SecuredUser(user, "UK*SOCCER*96");
    	}
    	
    	return admin;
    }
    
    /**
     * Changes the password for the specified user.
     * @param user
     * @param password The new password
     */
    public void changePassword(User user, String password) {
        DatabaseManager.getInstance().changePassword(user, password);
        if (users.containsKey(user.getID())) {
            users.get(user.getID()).setPassword(password);
        }
    }
    
    /**
     * Try to log in with the default user credentials (sa, empty password).
     */
    public boolean unsecureLogin() {
        try {
            return login("sa", "") != null && getUserCount() == 1;
        } catch (SecurityException se) {
            return false;
        }
    }
    
    public SecuredUser getUser(String token) {
    	SecurityToken st;
    	
    	for (SecuredUser user : users.values()) {
    		st = user.getSecurityToken();
    		
    		if (st.matches(token) && st.isValid())
    			return user;
    	}
    	
    	return null;
    }
    
    public boolean isLoggedIn(String token) {
        return getUser(token) != null;
    }
    
    public boolean isLoggedIn(SecuredUser su) {
        return users.containsValue(su);
    }
    
    public void logoff(User user) {
        users.remove(user.getID());
    }
    
    /**
     * Login process with user re-use.
     * @param clientKey
     * @param username
     * @param password
     * @return
     * @throws SecurityException
     */
	public SecuredUser login(String clientKey, String username, String password) throws SecurityException {
    	SecuredUser su = users.get(clientKey);
    	
    	if (su == null) {
    		
    		logger.debug("First time logon of user: " + username);
    		
    		return login(username, password);
    		
    	} else {
    		
    		if (su.getUsername().equals(username) && 
    		    su.getUser().getID().equals(clientKey) &&
    		  ((su.getPassword() == null && password == null) || su.getPassword().equals(password))) {
    	
    			// the user still exists. Check if the DB accepts the user as well.
    			Connection connection = DatabaseManager.getInstance().getConnection(username, password);
    			// if the database connection cannot be establish, the user is invalid.
    			if (connection == null) {
    				throw new SecurityException("Invalid user / password: " + su);
    			} else {
    				try {
    					connection.close();
    				} catch (SQLException se) {
    					logger.error("Failed to close connection after login", se);
    				}
    			}
    			
    			logger.debug("User " + su + " re-used");
    			
    			return su;
    		
	    	} else {
	    		throw new SecurityException("User registered but username / password combination is invalid: " + su);
	    	}
    	}
    }
    
    /**
     * Login process without user re-use.
     * @param username
     * @param password
     * @return
     * @throws SecurityException
     */
    @SuppressWarnings("resource")
	public SecuredUser login(String username, String password) throws SecurityException {
        Connection connection = DatabaseManager.getInstance().getConnection(username, password);
        
        if (connection == null) 
            throw new SecurityException(DcResources.getText("msgUserOrPasswordIncorrect"));
           
		try {
			connection.close();
		} catch (SQLException se) {
			logger.error("Failed to close connection after login", se);
		}
        
        ResultSet rs = null;
        Statement stmt = null;
        try {
            connection = DatabaseManager.getInstance().getAdminConnection();
            String sql = "select * from user where lower(loginname) = '" + username.toLowerCase() + "'";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            
            List<DcObject> users = DataManager.getInstance().convert(
            		rs, 
            		new int[] {User._ID, User._A_LOGINNAME, User._B_ENABLED, User._C_NAME, User._L_ADMIN});

			try {
				rs.close();
			} catch (SQLException se) {
				logger.error("Failed to close connection after login", se);
			}
            
            User user;
            if (users.size() == 1) {
                user = (User) users.get(0);
                sql = "select * from permission where userid = '" + user.getID() + "'";
                rs = stmt.executeQuery(sql);

                List<DcObject> permissions = DataManager.getInstance().convert(
                		rs, 
                		new int[] {Permission._ID, 
                                   Permission._A_PLUGIN, 
                                   Permission._B_FIELD, 
                                   Permission._C_MODULE, 
                                   Permission._D_VIEW,
                                   Permission._E_EDIT,
                                   Permission._F_USER});
                
                for (DcObject permission : permissions)
                    user.addChild(permission);
                
            } else {
                throw new SecurityException(DcResources.getText("msgUserOrPasswordIncorrect"));
            }
            
            if (!(Boolean) user.getValue(User._B_ENABLED))
                throw new SecurityException(DcResources.getText("msgLoginNotAllowed"));
            
            SecuredUser su = new SecuredUser(user, password);
            
            this.users.put(user.getID(), su);
            
            su.loadPermissions();
            
            return su;
            
        } catch (Exception e) {
            logger.info(e, e);
            throw new SecurityException(DcResources.getText("msgUserOrPasswordIncorrect"));
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                logger.info(e, e);
            }
        }
    }
    
    /**
     * Creates the default user. This user reflects the default SA account of the
     * HSQL database. No additional privileges need to be set.
     */
    private void createDefaultUser(String username, String password) {
        // default system administrator
        Connection connection = DatabaseManager.getInstance().getConnection("DC_ADMIN", "UK*SOCCER*96");
        if (connection == null) {
            User user = new User();
            user.setValue(User._A_LOGINNAME, "DC_ADMIN");
            user.setValue(User._B_ENABLED, Boolean.TRUE);
            user.setValue(User._L_ADMIN, Boolean.TRUE);
            DatabaseManager.getInstance().createUser(user, "UK*SOCCER*96");
        } else {
            try {
                connection.close();
            } catch (SQLException se) {
                logger.error(se, se);
            }
        }
        
        User user = new User();
        user.setIDs();
        user.setValue(User._A_LOGINNAME, username);
        user.setValue(User._B_ENABLED, Boolean.TRUE);
        user.setValue(User._L_ADMIN, Boolean.TRUE);
        user.setValue(User._C_NAME, "Administrator");
        user.setValue(User._D_DESCRIPTION, "The default users. Has all rights.");
        
        for (Permission permission : getDefaultPermissions())
            user.addChild(permission);

        // SA is the default user; only create the user if it is not equal to SA
        if (!username.equals("SA"))
        	DatabaseManager.getInstance().createUser(user, password);
        else if (!CoreUtilities.isEmpty(password))
        	DatabaseManager.getInstance().changePassword(user, password);
        
        try {
            user.setIDs();
            
            // Use the default administrator user
            User admin = new User();
            admin.setIDs();
            admin.setValue(User._A_LOGINNAME, "DC_ADMIN");
            SecuredUser su = new SecuredUser(admin , "UK*SOCCER*96");
            
            InsertQuery query = new InsertQuery(su, user);
            query.run();
            
            admin.cleanup();
        } catch (Exception e) {
            logger.error(e, e);
        }
    }  
    
    private Collection<Permission> getDefaultPermissions() {
        Collection<Permission> permissions = new ArrayList<Permission>();
        
        Permission permission;
        for (DcModule module : DcModules.getSecuredModules()) {
            for (DcField field : module.getFields()) {
                permission = (Permission) DcModules.get(DcModules._PERMISSION).getItem();
                permission.setIDs();
                permission.setValue(Permission._B_FIELD, Long.valueOf(field.getIndex()));
                permission.setValue(Permission._C_MODULE, Long.valueOf(field.getModule()));
                permission.setValue(Permission._D_VIEW, Boolean.TRUE);
                permission.setValue(Permission._E_EDIT, Boolean.TRUE);
                permissions.add(permission);
            }
        }
        
        // It's assumed the server will also hold the plugins.
        // This way, only plugins as registered on the server will be available,
        for (RegisteredPlugin plugin : Plugins.getInstance().getRegistered()) {
            if (plugin.isAuthorizable()) {
                permission = (Permission) DcModules.get(DcModules._PERMISSION).getItem();
                permission.setIDs();
                permission.setValue(Permission._A_PLUGIN, plugin.getKey());
                permission.setValue(Permission._D_VIEW, Boolean.TRUE);
                permissions.add(permission);
            }
        }
        
        return permissions;
    }
    
    @SuppressWarnings("resource")
	private int getUserCount() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        int users = 0;
        try {
            connection = DatabaseManager.getInstance().getConnection("DC_ADMIN", "UK*SOCCER*96");
            connection = connection == null ? DatabaseManager.getInstance().getConnection("SA", "") : connection;
            
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT ID FROM user");
            while (rs.next())
                users++;
            
        } catch (SQLException se) {
            logger.error(se, se);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                logger.debug("Failed to close database resource", e);
            }
        }
        return users;
    }
    
    public void initialize(String username, String password) {
        
        if (DcConfig.getInstance().getOperatingMode() == DcConfig._OPERATING_MODE_CLIENT)
            return;
        
        Connection connection = DatabaseManager.getInstance().getConnection("SA", "");
        if (connection != null) { // default user present.
            try {
                createTables();
                if (getUserCount() == 0) // no user. create default.
                    createDefaultUser(username, password);
                
            } catch (Exception e) {
                logger.error(e, e);
            } finally {
                try {
                    if (connection != null) connection.close();
                } catch (SQLException se) {
                    logger.debug("Failed to close database connection", se);
                }
            }
        }
    }
    
    private void createTables() {
        try {
            CreateQuery query = new CreateQuery(DcModules._USER);
            query.setLog(false);
            query.run();
        } catch (Exception se) {
        	logger.debug(se, se);
        }

        CreateQuery query = new CreateQuery(DcModules._PERMISSION);
        query.setLog(false);
        query.run();
    }
}
