package es.um.asio.service.service.impl.trellis;

import es.um.asio.service.constants.Constants;
import es.um.asio.service.service.trellis.TrellisCache;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;



@Service
public class TrellisCacheImpl implements TrellisCache {


    // key = container name
    private Map<String, Object> cacheTrellisContainers;

    // key property name
    private Map<String, Object> cacheProperties;

    // key entityId-className
    private Map<String, Object> cacheCanonicalLocalUris;

    public TrellisCacheImpl() {
        this.cacheTrellisContainers = new HashMap<>();
        this.cacheProperties = new HashMap<>();
        this.cacheCanonicalLocalUris = new HashMap<>();
    }

    @Override
    public Object find(String key, String cacheName) {
        Object result = null;
        if (this.retrieveCache(cacheName) != null) {
            result = this.retrieveCache(cacheName).get(key);
        }
        return result;
    }

    @Override
    public void saveInCache(String key, Object value, String cacheName) {
        if (this.retrieveCache(cacheName) != null) {
            this.retrieveCache(cacheName).put(key, value);
        }
    }

    private Map<String, Object> retrieveCache(String cacheName) {
        switch (cacheName) {
            case Constants.CACHE_TRELLIS_CONTAINER:
                return cacheTrellisContainers;
            case Constants.CACHE_PROPERTIES:
                return cacheProperties;
            case Constants.CACHE_CANONICAL_LOCAL_URIS:
                return cacheCanonicalLocalUris;
            default:
                return new HashMap<>();
        }
    }
}
