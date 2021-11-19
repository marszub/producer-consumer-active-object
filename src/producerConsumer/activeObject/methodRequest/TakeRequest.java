package producerConsumer.activeObject.methodRequest;

import producerConsumer.activeObject.Future;
import producerConsumer.activeObject.Servant;
import producerConsumer.activeObject.resource.Resource;

import java.util.List;

// Representation of Proxy.Take method
public class TakeRequest implements MethodRequest {
    private final int count;
    private final Future<List<Resource>> future;

    public TakeRequest(int count) {
        this.count = count;
        future = new Future<>();
    }

    @Override
    public void call(Servant servant) {
        List<Resource> taken = servant.take(count);
        future.set(taken);
    }

    @Override
    public boolean guard(Servant servant) {
        return servant.hasResources(count);
    }

    public Future<List<Resource>> getFuture() {
        return future;
    }
}
