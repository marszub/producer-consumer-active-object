package activeObject;

// Future object is returned for each call of Proxy method
// Is used to store returned value of methods called on the Servant
// and information if the response is ready
public class Future<T> {
    private T resource;
    private Boolean isReady;

    public Future() {
        isReady = false;
    }

    public synchronized void set(T resource) {
        this.resource = resource;
        isReady = true;
        this.notifyAll();
    }

    //Synchronized just to allow wait() for inactive waiting
    public synchronized T get() {
        try {
            while (!isReady)
                this.wait();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resource;
    }

    public boolean isReady() {
        return this.isReady;
    }
}
