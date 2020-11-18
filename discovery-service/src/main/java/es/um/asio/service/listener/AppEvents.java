package es.um.asio.service.listener;

public interface AppEvents {

    public void onCachedDataIsReady();

    public void onRealDataIsReady();

    public void onElasticSearchIsReady();
}
