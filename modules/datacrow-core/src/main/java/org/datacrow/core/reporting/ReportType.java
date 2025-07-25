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

package org.datacrow.core.reporting;

import org.datacrow.core.resources.DcResources;


public enum ReportType {
	
	PDF("pdf", DcResources.getText("lblReportTypePDF")),
	HTML("html",DcResources.getText("lblReportTypeHTML")),
	RTF("rtf", DcResources.getText("lblReportTypeRTF")),
	DOCX("docx", DcResources.getText("lblReportTypeDOCX")),
	XLSX("xlsx", DcResources.getText("lblReportTypeXLSX"));
	
	private final String extension;
	private final String name;
	
    ReportType(String extention, String name) {
    	this.extension = extention;
    	this.name = name;
    }

	public String getExtension() {
		return extension;
	}

	public String getName() {
		return name;
	}
    
	@Override
	public String toString() {
		return name;
	}
}
