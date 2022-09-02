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

package net.datacrow.web.util;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

import net.datacrow.core.DcRepository;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.utilities.StringUtils;
import net.datacrow.web.model.Field;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class WebUtilities {
    
    private transient static Logger logger = Logger.getLogger(WebUtilities.class.getName());
    
    public static String getValue(DcObject dco, Field f, Object value) {
        return getValue(dco, f.getIndex(), f.getMaxTextLength(), value);
    }

    public static Object getBean(String name) throws ClassNotFoundException {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Object bean = elContext.getELResolver().getValue(elContext, null, name);
        
        if (bean == null)
            throw new ClassNotFoundException("Bean " + name + " could not be found");
        
        return bean;
    }

    private static String getValue(DcObject dco, int fieldIdx, int maxTextLength, Object value) {
        DcField field = dco.getField(fieldIdx);
        String s = "";
        s = value != null && field.getValueType() == DcRepository.ValueTypes._PICTURE ? 
                    "/mediaimages/" + ((Picture) value).getScaledFilename() : 
                    dco.getDisplayString(field.getIndex());

        if (maxTextLength != 0 && field.getValueType() != DcRepository.ValueTypes._PICTURE)
            s = StringUtils.concatUserFriendly(s, maxTextLength);
        
        return s;
    }
    
    public static void log(int level, String msg) {
        log(level, msg, null);
    }
    
    public static void log(int level, Exception e) {
        log(level, e.getMessage(), e);
    }
    
    public static void log(int level, String msg, Exception e) {
        if (level == Level.DEBUG_INT)
            logger.debug(e, e);
        if (level == Level.ERROR_INT)
            logger.error(e, e);
        if (level == Level.WARN_INT)
            logger.warn(e, e);
        if (level == Level.INFO_INT)
            logger.info(e, e);        
    }
}
