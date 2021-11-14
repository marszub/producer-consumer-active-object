package activeObject;

import activeObject.methodRequest.MethodRequest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//Todo: Implement everything that's happening it the second thread - calling servant etc.
public class Scheduler {
    private final Servant servant;

    private Queue<MethodRequest> callQueue;
    private Queue<MethodRequest> priorityCallQueue;

    private Lock primaryQueueLock = new ReentrantLock();
    private Condition queueEmptyCondition = primaryQueueLock.newCondition();

    public Scheduler(Servant servant)
    {
        this.servant = servant;
        this.callQueue = new LinkedList<>();
        this.priorityCallQueue = new LinkedList<>();
    }

    public void enqueue(MethodRequest request)
    {
        primaryQueueLock.lock();
        callQueue.offer(request);
        queueEmptyCondition.signal();
        primaryQueueLock.unlock();
    }
}
