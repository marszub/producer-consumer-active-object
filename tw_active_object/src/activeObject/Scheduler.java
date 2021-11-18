package activeObject;

import activeObject.methodRequest.MethodRequest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler implements Runnable {
    private final Servant servant;
    Thread schedulerThread;
    private final Queue<MethodRequest> callQueue;
    private final Queue<MethodRequest> priorityCallQueue;
    private final Lock primaryQueueLock = new ReentrantLock();
    private final Condition queueEmptyCondition = primaryQueueLock.newCondition();

    public Scheduler(Servant servant) {
        this.servant = servant;
        this.callQueue = new LinkedList<>();
        this.priorityCallQueue = new LinkedList<>();
        this.schedulerThread = new Thread(this);
        this.schedulerThread.setDaemon(true);
        this.schedulerThread.start();
    }

    public void enqueue(MethodRequest request) {
        //This operation is protected, because all client threads and the servant thread can touch this queue
        primaryQueueLock.lock();
        try {
            callQueue.offer(request);
            queueEmptyCondition.signal();
        } finally {
            primaryQueueLock.unlock();
        }
    }

    public void run() {
        while (true)
            this.dispatch();
    }

    private void dispatch() {
        if (tryExecutePriorityRequest())
            return;
        tryExecuteStandardRequest();
    }

    private MethodRequest getRequest() {
        //This is protected, because the primary queue can be touched by client threads
        primaryQueueLock.lock();
        try {
            waitTillNotEmpty();
            return callQueue.remove();
        } finally {
            primaryQueueLock.unlock();
        }
    }

    private void tryExecuteStandardRequest() {
        MethodRequest methodRequest = getRequest();
        if (!priorityCallQueue.isEmpty() && priorityCallQueue.peek().getClass() == methodRequest.getClass()) {
            priorityCallQueue.add(methodRequest);
            return;
        }
        if (methodRequest.guard(servant)) {
            methodRequest.call(servant);
        } else {
            priorityCallQueue.add(methodRequest);
        }
    }

    private boolean tryExecutePriorityRequest() {
        if (!priorityCallQueue.isEmpty() && priorityCallQueue.peek().guard(servant)) {
            priorityCallQueue.remove().call(servant);
            return true;
        }
        return false;
    }

    private void waitTillNotEmpty() {
        try {
            while (callQueue.isEmpty())
                queueEmptyCondition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
