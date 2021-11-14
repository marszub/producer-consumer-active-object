package active_object.method_request;

import active_object.Servant;

// For every method shared by Proxy, override to represent its call by an object.
public interface MethodRequest {
    void call(Servant servant);
    boolean guard(Servant servant);
}
