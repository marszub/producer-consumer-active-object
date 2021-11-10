import java.util.List;

public class StoreRequest implements MethodRequest{
    private final Servant servant;
    List<Resource> toStore;

    public StoreRequest(Servant servant, List<Resource> toStore){
        this.servant = servant;
        this.toStore = toStore;
    }

    @Override
    public void call() {
        servant.Store(toStore);
        // TODO: Return to future
    }

    @Override
    public boolean guard() {
        return servant.HasSpace(toStore.size());
    }
}
