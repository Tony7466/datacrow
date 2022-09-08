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

import org.datacrow.core.DcRepository;
import org.datacrow.core.services.Region;
import org.datacrow.core.services.plugin.IServer;
import org.datacrow.core.settings.Setting;
import org.datacrow.core.utilities.settings.DcSettings;

public abstract class AmazonServer implements IServer {

    private static final long serialVersionUID = -1442149752500760414L;

    private Collection<Region> regions = new ArrayList<Region>();
    
    public AmazonServer() {
        regions.add(new Region("us", "United States (english)", "ecs.amazonaws.com"));
        regions.add(new Region("uk", "United Kingdom (english)", "ecs.amazonaws.co.uk"));
        regions.add(new Region("de", "Germany (german)", "ecs.amazonaws.de"));
        regions.add(new Region("fr", "France (french)", "ecs.amazonaws.fr"));
        regions.add(new Region("ca", "Canada (english)", "ecs.amazonaws.ca"));
    }

    @Override
    public String getName() {
        return "Amazon";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    public Collection<Region> getRegions() {
        return regions;
    }

    @Override
    public Collection<Setting> getSettings() {
        Collection<Setting> settings = new ArrayList<Setting>();
//        settings.add(DcSettings.getSetting(DcRepository.Settings.stAwsAccessKeyId));
//        settings.add(DcSettings.getSetting(DcRepository.Settings.stAwsSecretKey));
        settings.add(DcSettings.getSetting(DcRepository.Settings.stAmazonRetrieveFeatureListing));
        settings.add(DcSettings.getSetting(DcRepository.Settings.stAmazonRetrieveEditorialReviews));
        settings.add(DcSettings.getSetting(DcRepository.Settings.stAmazonRetrieveUserReviews));
        return settings;
    }    

    @Override
    public String getUrl() {
        return "ecs.amazonaws.com";
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
