package active_object.method_request;

import active_object.Future;
import active_object.Servant;
import active_object.resource.Resource;

import java.util.List;

// Representation of Proxy.Store method
public class StoreRequest implements MethodRequest {
    private List<Resource> toStore;
    private Future<Integer> future;

    public StoreRequest(List<Resource> toStore){
        this.toStore = toStore;
    }

    @Override
    public void call(Servant servant) {
        servant.Store(toStore);
        // TODO: Return to future
    }

    @Override
    public boolean guard(Servant servant) {
        return servant.HasSpace(toStore.size());
    }

    public Future<Integer> getFuture()
    {
        return future;
    }
}
