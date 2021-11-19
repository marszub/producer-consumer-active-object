package producerConsumer.activeObject;

import producerConsumer.activeObject.methodRequest.StoreRequest;
import producerConsumer.activeObject.methodRequest.TakeRequest;
import producerConsumer.activeObject.resource.Resource;

import java.util.List;

public class Proxy {
    private final Scheduler scheduler;

    public Proxy(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Future<Void> put(List<Resource> resources) {
        StoreRequest request = new StoreRequest(resources);
        Future<Void> future = request.getFuture();
        scheduler.enqueue(request);
        return future;
    }

    public Future<List<Resource>> get(int count) {
        TakeRequest request = new TakeRequest(count);
        Future<List<Resource>> future = request.getFuture();
        scheduler.enqueue(request);
        return future;
    }
}
