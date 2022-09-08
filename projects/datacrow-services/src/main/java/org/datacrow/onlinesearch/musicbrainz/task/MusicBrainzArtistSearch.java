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

package org.datacrow.onlinesearch.musicbrainz.task;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.datacrow.core.http.HttpConnectionUtil;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.services.IOnlineSearchClient;
import org.datacrow.core.services.Region;
import org.datacrow.core.services.SearchMode;
import org.datacrow.core.services.plugin.IServer;

public class MusicBrainzArtistSearch extends MusicBrainzSearch {

    public MusicBrainzArtistSearch(IOnlineSearchClient listener, 
                                   IServer server, 
                                   Region region, 
                                   SearchMode mode,
                                   String query) {
        
        super(listener, server, region, mode, query); 
    }
    
    @Override
	protected DcObject getItem(Object id, boolean full) throws Exception {
		URL url = new URL(new MusicBrainzArtist(getServer().getUrl(), true).getLink(id));
		return getItem(url);
	}

    @Override
	protected DcObject getItem(URL url) throws Exception {
        String xml = HttpConnectionUtil.retrievePage(url);
		return new MusicBrainzArtist(getServer().getUrl(), true).parse(xml);
	}

    @Override
	protected Collection<Object> getItemKeys() {
		Collection<Object> ids = new ArrayList<Object>();
		
		try {
			String url = getAddress() + "/artist/?type=xml&limit=" + getMaximum() + "&name=" + getQuery();
			String page = HttpConnectionUtil.retrievePage(url);
			ids.addAll(getKeys(page, "artist"));
		} catch (Exception e) {
			listener.addError(e);
		}
			
		return ids;
	}
}
