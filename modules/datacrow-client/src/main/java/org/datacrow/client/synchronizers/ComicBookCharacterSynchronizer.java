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

package org.datacrow.client.synchronizers;

import org.datacrow.core.modules.DcModules;
import org.datacrow.core.resources.DcResources;
import org.datacrow.core.synchronizers.DefaultSynchronizer;
import org.datacrow.core.synchronizers.Synchronizer;

public class ComicBookCharacterSynchronizer extends DefaultSynchronizer {

    public ComicBookCharacterSynchronizer() {
        super(DcResources.getText("lblMassItemUpdate", DcModules.get(DcModules._COMICCHARACTER).getObjectName()),
              DcModules._COMICCHARACTER);
    }
    
    @Override
	public Synchronizer getInstance() {
		return new ComicBookCharacterSynchronizer();
	}
    
    @Override
    public String getHelpText() {
        return DcResources.getText("msgComicBookCharacterMassUpdateHelp");
    }
}
