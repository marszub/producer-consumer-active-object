import java.util.LinkedList;
import java.util.List;

public class Servant {
    private final int size;
    private final List<Resource> storage;

    public Servant(int storageSize){
        size = storageSize;
        storage = new LinkedList<>();
    }

    // Message queue operations
    public void Store(List<Resource> toStore){
        storage.addAll(toStore);
    }

    public List<Resource> Take(int count){
        List<Resource> out = storage.subList(0, count);
        storage.removeAll(out);
        return out;
    }

    // Predicates
    public boolean HasSpace(int count){
        return count <= size - storage.size();
    }

    public boolean HasResources(int count){
        return count <= storage.size();
    }
}
