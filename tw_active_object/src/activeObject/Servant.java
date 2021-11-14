package activeObject;

import activeObject.resource.Resource;

import java.util.LinkedList;
import java.util.List;

// Synchronous data structure on which operations served by the Active Object are performed.
public class Servant {
    private final int size;
    private final List<Resource> storage;

    public Servant(int storageSize){
        size = storageSize;
        storage = new LinkedList<>();
    }

    // Storage operations
    public void store(List<Resource> toStore){
        storage.addAll(toStore);
    }

    public List<Resource> take(int count){
        List<Resource> out = storage.subList(0, count);
        storage.removeAll(out);
        return out;
    }

    // Predicates
    public boolean hasSpace(int count){
        return count <= size - storage.size();
    }

    public boolean hasResources(int count){
        return count <= storage.size();
    }
}
