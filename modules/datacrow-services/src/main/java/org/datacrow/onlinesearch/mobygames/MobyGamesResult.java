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

package org.datacrow.onlinesearch.mobygames;

import java.util.ArrayList;
import java.util.Collection;

import org.datacrow.core.objects.DcObject;

public class MobyGamesResult {
    
    private final DcObject dco;
    
    private final int platformId;
    
    private final Collection<String> covers = new ArrayList<>();
    private final Collection<String> screenshots = new ArrayList<>();

    public MobyGamesResult(DcObject dco, int platformId) {
        this.dco = dco;
        this.platformId = platformId;
    }
    
    public DcObject getDco() {
        return dco;
    }
    
    public int getPlatformId() {
        return platformId;
    }
    
    public Collection<String> getScreenshots() {
        return screenshots;
    }
    
    public void addScreenshot(String link) {
        screenshots.add(link);
    }
    
    public Collection<String> getCovers() {
    	return covers;
    }
    
    public void addCover(String link) {
        covers.add(link);
    }
}
