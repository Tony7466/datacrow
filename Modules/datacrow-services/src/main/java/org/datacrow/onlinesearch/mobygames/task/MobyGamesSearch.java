package org.datacrow.onlinesearch.mobygames.task;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.datacrow.core.DcRepository;
import org.datacrow.core.http.HttpConnection;
import org.datacrow.core.http.HttpConnectionUtil;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.resources.DcResources;
import org.datacrow.core.services.IOnlineSearchClient;
import org.datacrow.core.services.SearchMode;
import org.datacrow.core.services.SearchTask;
import org.datacrow.core.services.SearchTaskUtilities;
import org.datacrow.core.services.plugin.IServer;
import org.datacrow.core.settings.DcSettings;
import org.datacrow.core.utilities.CoreUtilities;
import org.datacrow.onlinesearch.exception.ApiKeyException;
import org.datacrow.onlinesearch.mobygames.helpers.MobyGamesPlatform;

import com.google.gson.Gson;

public class MobyGamesSearch extends SearchTask {

    //private static Logger logger = DcLogManager.getLogger(MobyGamesSearch.class.getName());

    public MobyGamesSearch(
            IOnlineSearchClient listener, 
            IServer server, 
            SearchMode mode,
            String query,
            Map<String, Object> additionalFilters) {
        
        super(listener, server, null, mode, query, additionalFilters);
    }

    @Override
    protected DcObject getItem(Object key, boolean full) throws Exception {
        return (DcObject) key;
    }

    @Override
    protected DcObject getItem(URL url) throws Exception {
        return null;
    }

    @Override
    public String getWhiteSpaceSubst() {
        return "+";
    }

    @Override
    protected void preSearchCheck() {
        SearchTaskUtilities.checkForIsbn(this);
    }
    
    private String getBaseUrl() throws Exception {
        String apiKey = DcSettings.getString(DcRepository.Settings.stMobyGamesApiKey);
        if (CoreUtilities.isEmpty(apiKey)) {
            String msg = DcResources.getText("msgMobyGamesNoApiKeyDefined");
            throw new ApiKeyException(msg);
        } else {
            return "https://api.mobygames.com/v1/games?api_key=" + apiKey;
        }
    }
    
    @Override
    protected Collection<Object> getItemKeys() throws Exception {
        Collection<Object> keys = new ArrayList<Object>();
        
        String sUrl = getBaseUrl();
        sUrl += "&title=" + getQuery();
        
        Map<String, Object> additionalFilters = getAdditionalFilters();

        // TODO: save selected platform to settings
        
        if (additionalFilters != null) {
            MobyGamesPlatform platform =
                    (MobyGamesPlatform) additionalFilters.get(DcResources.getText("lblPlatform"));

            sUrl += "&platform=" + platform.getId();
        }

        URL url = new URL(sUrl);
        
        HttpConnection connection = HttpConnectionUtil.getConnection(url);
        String result = connection.getString(StandardCharsets.UTF_8);
        
        Gson gson = new Gson();
        
        /*
        Collection<String> googleBooks = StringUtils.getValuesBetween("\"books#volume\"", "\"books#volume\"", result);

        Software software;
        for (String googleBook : googleBooks) {
            software = new Software();
            
            String googleID = getValue("id", googleBook);
            
            book.addExternalReference(DcRepository.ExternalReferences._GOOGLE, googleID);
            book.setValue(DcObject._SYS_SERVICEURL, getValue("selfLink", googleBook));
            book.setValue(Book._A_TITLE, getValue("title", googleBook));
            book.setValue(Book._H_WEBPAGE, "http://books.google.com/books?id=" + googleID);
            
            book.createReference(Book._F_PUBLISHER, getValue("publisher", googleBook));
            
//            setDescription(googleBook, book);
//            setYear(googleBook, book);
//            setRating(googleBook, book);
//            setIsbn(googleBook, book);
//            setAuthors(googleBook, book);
//            setCategories(googleBook, book);
//            setPages(googleBook, book);
//            setImages(googleBook, book);
            
            keys.add(book);
        } */
        return keys;
    }
}