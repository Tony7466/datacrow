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

package org.datacrow.onlinesearch.imdb.server;

import java.util.ArrayList;
import java.util.Collection;

import org.datacrow.core.DcRepository;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.services.IOnlineSearchClient;
import org.datacrow.core.services.Region;
import org.datacrow.core.services.SearchMode;
import org.datacrow.core.services.SearchTask;
import org.datacrow.core.settings.DcSettings;
import org.datacrow.core.settings.Setting;
import org.datacrow.onlinesearch.imdb.task.ImdbMovieSearch;

public class ImdbMovieServer extends ImdbServer {

    private static final long serialVersionUID = 1622578031165982693L;

    private Collection<SearchMode> modes = new ArrayList<SearchMode>();
    
    public ImdbMovieServer() {
        super();
    }
    
    @Override
    public Collection<Setting> getSettings() {
        Collection<Setting> settings = super.getSettings();
        settings.add(DcSettings.getSetting(DcRepository.Settings.stImdbGetOriginalTitle));
        return settings;
    }

    @Override
    public int getModule() {
        return DcModules._MOVIE;
    }

    @Override
    public Collection<SearchMode> getSearchModes() {
        return modes;
    }
    
    @Override
    public boolean isFullModeOnly() {
        return false;
    }
        
    @Override
    public SearchTask getSearchTask(IOnlineSearchClient listener,
                                    SearchMode mode, 
                                    Region region, 
                                    String query,
                                    DcObject client) {
        ImdbMovieSearch task = new ImdbMovieSearch(listener, this, region, mode, query);
        task.setClient(client);
        return task;
    }
}
