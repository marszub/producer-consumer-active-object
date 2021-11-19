package producerConsumer.activeObject;

// Future object is returned for each call of Proxy method
// Is used to store returned value of methods called on the Servant
// and information if the response is ready
public class Future<T> {
    private T response;
    private Boolean isReady;

    public Future() {
        isReady = false;
    }

    public void set(T resource) {
        this.response = resource;
        isReady = true;
    }

    //Synchronized just to allow wait() for inactive waiting
    public T get() {
        return response;
    }

    public boolean isReady() {
        return this.isReady;
    }
}
