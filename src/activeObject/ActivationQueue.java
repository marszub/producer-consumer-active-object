package activeObject;

import activeObject.methodRequest.MethodRequest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ActivationQueue {
    private final Queue<MethodRequest> queue;

    private final Lock lock = new ReentrantLock();
    private final Condition queueEmpty = lock.newCondition();

    ActivationQueue(){
        queue = new LinkedList<>();
    }

    public void enqueue(MethodRequest request) {
        //This operation is protected, because all client threads and the servant thread can touch this queue
        lock.lock();
        try {
            queue.offer(request);
            queueEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public MethodRequest dequeue() {
        //This is protected, because the primary queue can be touched by client threads

        lock.lock();
        try {
            waitForRequest();
            return queue.remove();
        } finally {
            lock.unlock();
        }
    }

    private void waitForRequest() {
        try {
            while (queue.isEmpty())
                queueEmpty.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
