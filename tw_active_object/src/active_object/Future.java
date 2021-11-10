package active_object;

import active_object.resource.Resource;

public class Future<T extends Resource> {
    private T resource;
    private Boolean isReady;

    public Future(){
        isReady = false;
    }

    public void Set(T resource){
        this.resource = resource;
        isReady = true;
    }

    public T Get(){
        return resource;
    }

    public boolean IsReady(){
        return isReady;
    }
}
