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

package net.datacrow.client.connector;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcLogManager;
import net.datacrow.core.server.Connector;
import net.datacrow.core.server.DcServerConnection;
import net.datacrow.core.server.requests.ClientRequest;
import net.datacrow.core.server.response.IServerResponse;

public class ClientRequestHandler {
	
    private transient static Logger logger = DcLogManager.getLogger(ClientRequestHandler.class);

	private ClientRequest cr;
    
	public ClientRequestHandler(ClientRequest cr) {
		this.cr = cr;
		JsonReader.setUseUnsafe(true);
	}
	
	/** 
	 * One method to handle them all.
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("resource")
    public synchronized IServerResponse process() throws IOException, ClassNotFoundException {
		IServerResponse response = null;
		DcServerConnection connection = null;
		
		try {
    		Connector conn = DcConfig.getInstance().getConnector();
    		connection = conn.getServerConnection();
    		connection.setAvailable(false);
    		
    		JsonWriter jw = new JsonWriter(connection.getOutputStream());
    		jw.write(cr);
    		jw.flush();
    		
    		JsonReader jr = new JsonReader(connection.getInputStream());
    		response = (IServerResponse) jr.readObject();
    		
    		logger.debug("Client has received: " + response);
		} catch (Exception e) {
		    logger.error("Failed to connect to server", e);
		}
		
		if (connection != null)
		    connection.setAvailable(true);
		
		return response;
	}
}
