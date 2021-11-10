package active_object.method_request;

// For every method shared by Proxy, override to represent its call by an object.
public interface MethodRequest {
    void call();
    boolean guard();
}
