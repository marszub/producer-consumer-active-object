package active_object.method_request;

import active_object.Servant;
import active_object.resource.Resource;

import java.util.List;

public class TakeRequest implements MethodRequest {
    private final Servant servant;
    private final int count;

    public TakeRequest(Servant servant, int count){
        this.servant = servant;
        this.count = count;
    }

    @Override
    public void call() {
        List<Resource> taken = servant.Take(count);
        // TODO: Return to future
    }

    @Override
    public boolean guard() {
        return servant.HasResources(count);
    }
}
