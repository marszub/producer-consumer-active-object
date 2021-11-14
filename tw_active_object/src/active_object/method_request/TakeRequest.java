package active_object.method_request;

import active_object.Future;
import active_object.Servant;
import active_object.resource.Resource;

import java.util.List;

// Representation of Proxy.Take method
public class TakeRequest implements MethodRequest {
    private final int count;
    private Future<List<Resource>> future;

    public TakeRequest(int count){
        this.count = count;
    }

    @Override
    public void call(Servant servant) {
        List<Resource> taken = servant.Take(count);
        // TODO: Return to future
    }

    @Override
    public boolean guard(Servant servant) {
        return servant.HasResources(count);
    }

    public Future<List<Resource>> getFuture()
    {
        return future;
    }
}
