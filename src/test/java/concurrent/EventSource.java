package concurrent;


public interface EventSource {
    void addListener(EventListener listener);
    void notifyListener();

    void registerListener(EventListener eventListener);
}
