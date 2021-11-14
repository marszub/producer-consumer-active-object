package activeObject.methodRequest;

import activeObject.Future;
import activeObject.Servant;
import activeObject.resource.Resource;

import java.util.List;

// Representation of Proxy.Store method
public class StoreRequest implements MethodRequest {
    private List<Resource> toStore;
    private Future<Void> future;

    public StoreRequest(List<Resource> toStore){
        this.toStore = toStore;
    }

    @Override
    public void call(Servant servant) {
        servant.store(toStore);
        future.set(null);
    }

    @Override
    public boolean guard(Servant servant) {
        return servant.hasSpace(toStore.size());
    }

    public Future<Void> getFuture()
    {
        return future;
    }
}
