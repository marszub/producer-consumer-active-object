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
    private Lock lock = new ReentrantLock();
    private Condition notReady = lock.newCondition();

    public Future(){
        isReady = false;
    }

    public void set(T resource){
        lock.lock();
        try {
            this.resource = resource;
            isReady = true;
            notReady.signal();
        } finally {
            lock.unlock();
        }

    }

    public T get() throws InterruptedException {
        lock.lock();
        try {
            while (!isReady)
                notReady.await();
            return resource;
        } finally {
            lock.unlock();
        }

    }

    public boolean isReady(){
        return isReady;
    }
}
