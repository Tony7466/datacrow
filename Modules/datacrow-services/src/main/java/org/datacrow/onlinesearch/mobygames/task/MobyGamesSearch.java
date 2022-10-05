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
        
        MobyGamesResult mgr = (MobyGamesResult) key;
        DcObject item = mgr.getDco();
        
        String serviceUrl = (String) item.getValue(Software._SYS_SERVICEURL);
        
        waitBetweenRequest();
        
        URL url = new URL(serviceUrl);
        HttpConnection connection = HttpConnectionUtil.getConnection(url);
        String result = connection.getString(StandardCharsets.UTF_8);
        
        Gson gson = new Gson();
        Map<?, ?> game = gson.fromJson(result, Map.class);
        
        setAttributes(game, item);
        setScreenshots(mgr, item);
        
        setServiceInfo(item);
        
        
        return item; 
    }

    @Override
    protected DcObject getItem(URL url) throws Exception {
        return null;
    }

    @Override
    public String getWhiteSpaceSubst() {
        return "%20";
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
        
        if (additionalFilters != null) {
            MobyGamesPlatform platform =
                    (MobyGamesPlatform) additionalFilters.get(DcResources.getText("lblPlatform"));
    
            if (platform != null && !CoreUtilities.isEmpty(platform.getId()))
                sUrl += "&platform=" + platform.getId();
        }
            
        waitBetweenRequest(); // prevent button smashing
        
        URL url = new URL(sUrl);
        
        HttpConnection connection = HttpConnectionUtil.getConnection(url);
        String result = connection.getString(StandardCharsets.UTF_8);
        
        Gson gson = new Gson();
        
        Map<?, ?> map = gson.fromJson(result, Map.class);
        
        @SuppressWarnings({"unchecked"})
        List<LinkedTreeMap> games = (List<LinkedTreeMap>) map.get("games");
        
        Software item;
        int mobygamesId;
        int count = 0;

        for (LinkedTreeMap game : games) {
            item = new Software();
            
            mobygamesId = ((Double) game.get("game_id")).intValue();
            
            item.addExternalReference(DcRepository.ExternalReferences._MOBYGAMES, String.valueOf(mobygamesId));
            
            setTitle(game, item);
            setUrl(game, item);
            setDescription(game, item);
            setRating(game, item);
            setCategories(game, item);
            setPlatformDetails(game, item, mobygamesId);
            setPictureFront(game, item);

            MobyGamesResult mgr = new MobyGamesResult(item);
            setScreenshots(game, mgr);
            
            keys.add(mgr);
            
            count++;
            
            if (count == getMaximum()) break;
        }
        
        return keys;
    }
    
    private void setAttributes(Map<?, ?> game, DcObject item) {
        
        List attributes = (List) game.get("attributes");
        
        LinkedTreeMap attribute;
        for (Object o : attributes) {
            attribute = (LinkedTreeMap) o;
            
            // multiplayer stuff
            if (attribute.get("attribute_category_name").equals("Multiplayer Game Modes")) {
                item.setValue(Software._AB_MULTI, Boolean.TRUE);

                if (attribute.get("attribute_name").equals("Team"))
                    item.setValue(Software._AA_COOP, Boolean.TRUE);
            }

            // storage medium
            if (attribute.get("attribute_category_name").equals("Media Type")) {
                item.createReference(Software._W_STORAGEMEDIUM, attribute.get("attribute_name"));
            }
        }
    }
    
    private void setScreenshots(MobyGamesResult mgr, DcObject item) {
        int[] fields = new int[] {Software._P_SCREENSHOTONE, Software._Q_SCREENSHOTTWO, Software._R_SCREENSHOTTHREE};
        int fieldIdx = 0;
        
        byte[] image;
        for (String link : mgr.getScreenshotLinks()) {
            image = getImageBytes(link);
            if (image != null)
                item.setValue(fields[fieldIdx++], image);
            
            if (fieldIdx > 2) break;
        }
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
            link = link.replace("http://", "https://");
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
        
        // waitBetweenRequest();
                  
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
    private void setPlatformDetails(LinkedTreeMap game, Software item, int mobygamesId) {
        List<LinkedTreeMap> platforms = (List<LinkedTreeMap>) game.get("platforms");
        
        if (platforms == null)
            return;
        
        for (LinkedTreeMap platform : platforms) {
            int platformId = ((Double) platform.get("platform_id")).intValue();
            String platformName = (String) platform.get("platform_name");
            String releasedOn = (String) platform.get("first_release_date");

            item.createReference(Software._H_PLATFORM, platformName);
            
            String serviceUrl = "https://api.mobygames.com/v1/games/" + mobygamesId 
                    + "/platforms/" + platformId + "?api_key=jI2EJMdA7RG8d/vl4uf3Aw==";
           
            item.setValue(Software._SYS_SERVICEURL, serviceUrl);
            
            if (releasedOn != null && releasedOn.length() >= 4)
                item.setValue(Software._C_YEAR, releasedOn.substring(0, 4));
            
            break;
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