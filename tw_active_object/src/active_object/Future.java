package active_object;

import active_object.resource.Resource;

// TODO: Add inactive waiting for resource

// Future object is returned for each call of Proxy method
// Is used to store returned value of methods called on the Servant
// and information if the response is ready
public class Future<T> {
    private T resource;
    private Boolean isReady;

    public Future(){
        isReady = false;
    }

    public void set(T resource){
        this.resource = resource;
        isReady = true;
    }

    public T get(){
        return resource;
    }

    public boolean isReady(){
        return isReady;
    }
}
