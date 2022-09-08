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

package org.datacrow.onlinesearch.amazon.server;

import java.util.ArrayList;
import java.util.Collection;

import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.helpers.Movie;
import org.datacrow.core.services.IOnlineSearchClient;
import org.datacrow.core.services.Region;
import org.datacrow.core.services.SearchMode;
import org.datacrow.core.services.SearchTask;
import org.datacrow.onlinesearch.amazon.mode.ItemLookupSearchMode;
import org.datacrow.onlinesearch.amazon.mode.KeywordSearchMode;
import org.datacrow.onlinesearch.amazon.task.AmazonMovieSearch;

public class AmazonMovieServer extends AmazonServer {
    
    private static final long serialVersionUID = -6398413413515413321L;

    Collection<SearchMode> modes = new ArrayList<SearchMode>();
    
    public AmazonMovieServer() {
        super();

        modes.add(new KeywordSearchMode("DVD", "DVD", Movie._A_TITLE));
        modes.add(new KeywordSearchMode("VHS", "VHS", Movie._A_TITLE));
        modes.add(new ItemLookupSearchMode("Video", ItemLookupSearchMode.getDescription(ItemLookupSearchMode._EAN), ItemLookupSearchMode._EAN, Movie._12_EAN));
        modes.add(new ItemLookupSearchMode("Video", ItemLookupSearchMode.getDescription(ItemLookupSearchMode._UPC), ItemLookupSearchMode._UPC, Movie._12_EAN));
        modes.add(new ItemLookupSearchMode("", ItemLookupSearchMode.getDescription(ItemLookupSearchMode._ASIN), ItemLookupSearchMode._ASIN, DcObject._SYS_EXTERNAL_REFERENCES));
    }
    
    @Override
    public int getModule() {
        return DcModules._MOVIE;
    }
    
    @Override
    public boolean isFullModeOnly() {
        return false;
    }
    
    @Override
    public Collection<SearchMode> getSearchModes() {
        return modes;
    }

    @Override
    public SearchTask getSearchTask(IOnlineSearchClient listener, SearchMode mode, Region region, String query, DcObject client) {
        AmazonMovieSearch task = new AmazonMovieSearch(listener, this, region, mode, query);
        task.setClient(client);
        return task;
    }
}
