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

import java.awt.Image;
import java.io.File;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.logging.log4j.Level;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.web.DcBean;
import net.datacrow.web.model.Field;
import net.datacrow.web.model.Item;
import net.datacrow.web.model.Picture;
import net.datacrow.web.util.WebUtilities;

@ManagedBean
@SessionScoped
public class PictureBean extends DcBean {

    private static final long serialVersionUID = 1L;

    private Field field;
    
    private File tempDir;
    
    private UploadedFile uploadedFile;
    
    public PictureBean() {
        String property = "java.io.tmpdir";
        tempDir = new File(System.getProperty(property), "datacrow");
        tempDir.mkdirs();
    }
    
    public void setField(Field field) {
        this.field = field;
    }
    
    public Field getField() {
        return field;
    }
    
    public void save() {}
    
    public void handleFileUpload(FileUploadEvent event) {
        this.uploadedFile = event.getFile();
        
        try {
            boolean isIcon = field.getType() == Field._ICON;
            
            ItemBean itemBean = (ItemBean) WebUtilities.getBean("editItemBean");
            Item item = itemBean.getItem();
            
            String filename = isIcon ? "icon_" + item.getID() : item.getID() + "_" + field.getSystemName();
            
            Picture p = new Picture(isIcon, filename);
            p.setContents(uploadedFile.getContents());
            
            File fileLarge = new File(tempDir, filename + ".jpg");
            File fileSmall;

            if (isIcon) {
                fileSmall = fileLarge;
                Image image = CoreUtilities.getScaledImage((byte[]) p.getContents(), 16, 16);
                byte[] bytes = CoreUtilities.getBytes(new DcImageIcon(image));
                CoreUtilities.writeToFile(bytes, fileLarge);
            } else {
                fileSmall = new File(tempDir, filename + "_small.jpg");
                CoreUtilities.writeToFile(p.getContents(), fileLarge);
                CoreUtilities.writeScaledImageToFile(new DcImageIcon(p.getContents()), fileSmall);
            }

            fileSmall.deleteOnExit();
            fileLarge.deleteOnExit();
            
            p.setFileLarge(fileLarge);
            p.setFileSmall(fileSmall);

            p.setEdited(true);
            
            field.setValue(p);
            field.setChanged(true);
            
        } catch (Exception e) {
            WebUtilities.log(Level.ERROR, e);
        }
    }
}
