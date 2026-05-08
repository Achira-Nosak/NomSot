package Model;

public interface IResourceProducer {
    void produceResources(boolean[][] powerMap, boolean[][] waterMap);
    boolean isNetworkConnected();
}
