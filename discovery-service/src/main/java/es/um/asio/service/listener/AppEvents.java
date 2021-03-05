package es.um.asio.service.listener;

/**
 * Interface from App Events.
 */
public interface AppEvents {

    /**
     * Is called when the cached data is ready
     */
    public void onCachedDataIsReady();

    /**
     * Is called when the real data is ready
     */
    public void onRealDataIsReady();

    /**
     * Is called when the elasticsearch data is ready
     */
    public void onElasticSearchIsReady();
}
