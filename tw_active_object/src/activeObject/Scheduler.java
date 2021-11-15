package activeObject;

import activeObject.methodRequest.MethodRequest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler implements Runnable {
    private final Servant servant;

    private Queue<MethodRequest> callQueue;
    private Queue<MethodRequest> priorityCallQueue;

    private Lock primaryQueueLock = new ReentrantLock();
    private Condition queueEmptyCondition = primaryQueueLock.newCondition();

    Thread schedulerThread;

    public Scheduler(Servant servant)
    {
        this.servant = servant;
        this.callQueue = new LinkedList<>();
        this.priorityCallQueue = new LinkedList<>();
        this.schedulerThread = new Thread(this);
        this.schedulerThread.setDaemon(true);
        this.schedulerThread.start();
    }

    public void enqueue(MethodRequest request)
    {
        primaryQueueLock.lock();
        try {
            callQueue.offer(request);
            queueEmptyCondition.signal();
        } finally {
            primaryQueueLock.unlock();
        }
    }

    public void run ()
    {
        while (true) {
            this.dispatch();
        }
    }

    private void dispatch()
    {
        tryExecutePriorityRequest();
        tryExecuteStandardRequest();
    }

    private MethodRequest getRequest() {
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

    private void tryExecutePriorityRequest() {
        if (!priorityCallQueue.isEmpty()) {
            if (priorityCallQueue.peek().guard(servant))
                priorityCallQueue.remove().call(servant);
        }
    }

    private void waitTillNotEmpty()
    {
        try {
            while (callQueue.isEmpty())
                queueEmptyCondition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
