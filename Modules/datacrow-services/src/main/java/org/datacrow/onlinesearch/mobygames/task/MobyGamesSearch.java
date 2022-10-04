package org.datacrow.onlinesearch.mobygames.task;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.datacrow.core.DcRepository;
import org.datacrow.core.http.HttpConnection;
import org.datacrow.core.http.HttpConnectionUtil;
import org.datacrow.core.log.DcLogManager;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.helpers.Software;
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
import org.datacrow.onlinesearch.mobygames.helpers.MobyGamesResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

@SuppressWarnings("rawtypes")
public class MobyGamesSearch extends SearchTask {

    private static Logger logger = DcLogManager.getLogger(MobyGamesSearch.class.getName());

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
        
        return ((MobyGamesResult) key).getDco();
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
        
        MobyGamesPlatform platform =
                (MobyGamesPlatform) additionalFilters.get(DcResources.getText("lblPlatform"));

        sUrl += "&platform=" + platform.getId();

        URL url = new URL(sUrl);
        
        HttpConnection connection = HttpConnectionUtil.getConnection(url);
        String result = connection.getString(StandardCharsets.UTF_8);
        
        Gson gson = new Gson();
        
        Map<?, ?> map = gson.fromJson(result, Map.class);
        
        @SuppressWarnings({"unchecked"})
        List<LinkedTreeMap> games = (List<LinkedTreeMap>) map.get("games");
        
        Software item;
        String serviceUrl;

        for (LinkedTreeMap game : games) {
            item = new Software();
            
            setTitle(game, item);
            setUrl(game, item);
            setDescription(game, item);
            setRating(game, item);
            setCategories(game, item);
            setYear(game, item, platform);
            
            item.createReference(Software._H_PLATFORM, platform.getDescription());
            
            setPictureFront(game, item);
            
            serviceUrl = "https://api.mobygames.com/v1/games/" + 
                    game.get("game_id") + "/platforms/" + platform.getId() + 
                    "?api_key=jI2EJMdA7RG8d/vl4uf3Aw==";
            
            
            item.setValue(Software._SYS_SERVICEURL, serviceUrl);

            MobyGamesResult mgr = new MobyGamesResult(item);
            setScreenshots(game, mgr);
            
            keys.add(mgr);
        }
        
        return keys;
    }
    
    private void setTitle(LinkedTreeMap game, Software item) {
        item.setValue(Software._A_TITLE, game.get("title"));
    }

    private void setUrl(LinkedTreeMap game, Software item) {
        String url = (String) game.get("moby_url");
                
        if (!CoreUtilities.isEmpty(url))
            item.setValue(Software._I_WEBPAGE, url);
    }

    @SuppressWarnings("unchecked")
    private void setScreenshots(LinkedTreeMap game, MobyGamesResult result) {
        List<LinkedTreeMap> screenshots = (List<LinkedTreeMap>) game.get("sample_screenshots");        

        if (screenshots == null) return;
        
        for (LinkedTreeMap screenshot : screenshots) {
            String link = (String) screenshot.get("image");
            result.addScreenshot(link);
        }
    }
    

    private void setPictureFront(LinkedTreeMap game, Software item) {
        LinkedTreeMap pic = (LinkedTreeMap) game.get("sample_cover");
                
        if (!CoreUtilities.isEmpty(pic)) {
            String link = (String) pic.get("image");
            byte[] image = getImageBytes(link);
            if (image != null)
                item.setValue(Software._M_PICTUREFRONT, image);
        }
    }
    
    private byte[] getImageBytes(String url) {
        url = url.replace("http://", "https://");
        
        try {
            sleep(1000);
        } catch (InterruptedException ie) {
            logger.debug("Could not wait during image retrieval");
        }
                  
        try {
            if (url != null && url.length() > 0) {
                byte[] b = HttpConnectionUtil.retrieveBytes(url);
                if (b != null && b.length > 50)
                    return b;
            }
        } catch (Exception e) {
            logger.debug("Cannot download image from [" + url + "]", e);
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private void setCategories(LinkedTreeMap game, Software item) {
        List<LinkedTreeMap> genres = (List<LinkedTreeMap>) game.get("genres");

        if (genres != null) {
            for (LinkedTreeMap genre : genres) {
                if (genre.get("genre_category").equals("Basic Genres")) 
                    item.createReference(Software._K_CATEGORIES, genre.get("genre_name"));
            }
        }
    }

    private void setRating(LinkedTreeMap game, Software item) {
        Double d = (Double) game.get("moby_score");
        if (d != null) {
            int rating = d.intValue()  * 2;
            item.setValue(Software._E_RATING, Integer.valueOf(rating));    
        }
    }

    @SuppressWarnings("unchecked")
    private void setYear(LinkedTreeMap game, Software item, MobyGamesPlatform mgp) {
        List<LinkedTreeMap> platforms = (List<LinkedTreeMap>) game.get("platforms");
        
        if (platforms == null)
            return;
        
        for (LinkedTreeMap platform : platforms) {
            if (platform.get("platform_id").equals(Double.valueOf(mgp.getId()))) {
                String releasedOn = (String) platform.get("first_release_date");
                if (releasedOn != null && releasedOn.length() >= 4)
                    item.setValue(Software._C_YEAR, releasedOn.substring(0, 4));
            }
        }
    }
    
    private void setDescription(LinkedTreeMap game, Software item) {
        String s = (String) game.get("description");
        if (!CoreUtilities.isEmpty(s)) {
            s = "<html><body>" + s + "</body></html>";
            Document doc = Jsoup.parse(s);
            s = doc.body().text();
            item.setValue(Software._B_DESCRIPTION, s);
        }
    }
}