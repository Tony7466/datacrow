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

package org.datacrow.onlinesearch.bol;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * TODO: reimplement or remove.
 * 
 * @author RJ
 *
 */
public class BolClient {

    /**
     * Constructs the test client.
     */
    public BolClient() {}

    /**
     * Searches.
     * 
     * @param term The search term (required).
     * @param categoryID The category id and refinements, separated by spaces (optional).
     */
    public String search (String term, String categoryID) throws IOException, URISyntaxException {
        /*List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
        queryParams.add(new BasicNameValuePair("term", term));

        if (categoryID != null)
            queryParams.add(new BasicNameValuePair("categoryId", categoryID));
        
        queryParams.add(new BasicNameValuePair("nrProducts", "10"));
        queryParams.add(new BasicNameValuePair("includeProducts", "TRUE"));
        queryParams.add(new BasicNameValuePair("includeCategories", "TRUE"));
        queryParams.add(new BasicNameValuePair("includeRefinements", "FALSE"));

        URIBuilder builder = new URIBuilder();
        builder.setScheme("https");
        builder.setHost("openapi.bol.com");
        builder.setPath("/openapi/services/rest/catalog/v3/searchresults/");
        builder.setQuery(URLEncodedUtils.format(queryParams, "UTF-8"));
                   
        HttpGet httpGet = new HttpGet(builder.build());
        
        HttpOAuthHelper au = new HttpOAuthHelper("application/xml");
        au.handleRequest(httpGet, accessKeyId, secretAccessKey, null, queryParams);

        HttpResponse httpResponse = httpClient.execute(httpGet);
        String xml = getXML(httpResponse);
        httpClient.getConnectionManager().shutdown();
        return xml;*/
        return "";
    }

    /**
     * Gets the product.
     * @param ID The product id (required).
     */
    public String getProduct(String ID) throws IOException, URISyntaxException {
        /*URIBuilder builder = new URIBuilder();
        builder.setScheme("https");
        builder.setHost("openapi.bol.com");
        builder.setPath("/openapi/services/rest/catalog/v3/products/" + ID);
        URI uri = builder.build();

        HttpGet httpGet = new HttpGet(uri);
        
        HttpOAuthHelper au = new HttpOAuthHelper("application/xml");
        au.handleRequest(httpGet, accessKeyId, secretAccessKey);

        HttpResponse httpResponse = httpClient.execute(httpGet);
        String xml = getXML(httpResponse);
        httpClient.getConnectionManager().shutdown();
        return xml;*/
        return "";
    }
}
