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

package org.datacrow.onlinesearch.comicvine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.datacrow.core.DcRepository;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.services.FilterField;
import org.datacrow.core.services.IOnlineSearchClient;
import org.datacrow.core.services.Region;
import org.datacrow.core.services.SearchMode;
import org.datacrow.core.services.SearchTask;
import org.datacrow.core.services.plugin.IServer;
import org.datacrow.core.settings.DcSettings;
import org.datacrow.core.settings.Setting;

public class ComicVineCharacterServer implements IServer {
    
    private static final long serialVersionUID = 1L;

    private final Collection<SearchMode> modes = new ArrayList<SearchMode>();
    private final Collection<Region> regions = new ArrayList<Region>();

    public ComicVineCharacterServer() {
    	regions.add(new Region("en", "English", "https://comicvine.gamespot.com/api/characters/"));
    }

    @Override
    public int getModule() {
        return DcModules._COMICCHARACTER;
    }

    @Override
	public boolean isEnabled() {
        return true;
    }
    
    @Override
    public Collection<Setting> getSettings() {
        Collection<Setting> settings = new ArrayList<>();
        settings.add(DcSettings.getSetting(DcRepository.Settings.stComicVineApiKey));
        settings.add(DcSettings.getSetting(DcRepository.Settings.stComicVineAddEnemiesAndFriends));        
        return settings;
    }
    
    @Override
    public String getName() {
        return "Comic Vine";
    }

    @Override
    public Collection<SearchMode> getSearchModes() {
        return modes;
    }

    @Override
    public String getUrl() {
        return "https://comicvine.gamespot.com/api/characters/";
    }
    
    @Override
    public Collection<FilterField> getFilterFields() {
        return new ArrayList<>();
    }
    
    @Override
    public SearchTask getSearchTask(
            IOnlineSearchClient listener,
            SearchMode mode,
            Region region,
            String query,
            Map<String, Object> additionalFilters,
            DcObject client) {
        
        ComicVineCharacterSearch task = new ComicVineCharacterSearch(listener, this, region, mode, query, additionalFilters);
        task.setClient(client);
        return task;
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public long getWaitTimeBetweenRequest() {
        return 1200l;
    }

	@Override
	public Collection<Region> getRegions() {
		return regions;
	}
}
