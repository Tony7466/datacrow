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

package net.datacrow.web.bean;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import net.datacrow.core.DcConfig;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.web.DcBean;

import org.primefaces.model.DefaultStreamedContent;

@ManagedBean 
@SessionScoped 
public class ImageBean extends DcBean {

    private static final long serialVersionUID = 1L;
    
    private File tempDir;
    
    public ImageBean() {
        String property = "java.io.tmpdir";
        tempDir = new File(System.getProperty(property), "datacrow");
        tempDir.mkdirs();
    }

    public DefaultStreamedContent getContent() { 
        FacesContext context = FacesContext.getCurrentInstance();
        String filename = context.getExternalContext().getRequestParameterMap().get("filename");
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE ||
            context.getRenderResponse() ||
            filename == null ||
            filename.equals("_small")) {
            
            return new DefaultStreamedContent();
        } else {
            File file = new File(DcConfig.getInstance().getImageDir(), filename + ".jpg");
            File fileTemp = new File(tempDir, filename + ".jpg");
            
            boolean fileExists = file.exists();
            boolean tempFileExists = fileTemp.exists();
            
            if (!fileExists && !tempFileExists) {
                return new DefaultStreamedContent();
            } else {
                try {
                    if (tempFileExists)
                        return new DefaultStreamedContent(new ByteArrayInputStream(CoreUtilities.readFile(fileTemp)), "image/png");
                    else
                        return new DefaultStreamedContent(new ByteArrayInputStream(CoreUtilities.readFile(file)), "image/png");
                } catch (Exception e) {
                    return new DefaultStreamedContent();
                }
            }
        }
    }
}