package producerConsumer.activeObject.methodRequest;

import producerConsumer.activeObject.Future;
import producerConsumer.activeObject.Servant;
import producerConsumer.activeObject.resource.Resource;

import java.util.List;

// Representation of Proxy.Store method
public class StoreRequest implements MethodRequest {
    private final List<Resource> toStore;
    private final Future<Void> future;

    public StoreRequest(List<Resource> toStore) {
        this.toStore = toStore;
        future = new Future<>();
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

    public Future<Void> getFuture() {
        return future;
    }
}
