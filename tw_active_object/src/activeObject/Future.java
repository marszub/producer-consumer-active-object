package activeObject;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Future object is returned for each call of Proxy method
// Is used to store returned value of methods called on the Servant
// and information if the response is ready
public class Future<T> {
    private T resource;
    private Boolean isReady;

    public Future(){
        isReady = false;
    }

    public synchronized void set(T resource){
        this.resource = resource;
        isReady = true;
        this.notifyAll();
    }

    public synchronized T get() throws InterruptedException {
        while (!isReady)
            this.wait();
        return resource;

    }

    public synchronized boolean isReady(){
        return this.isReady;
    }
}
