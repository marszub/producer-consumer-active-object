package activeObject;

import activeObject.methodRequest.MethodRequest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//Todo: Implement everything that's happening it the second thread - calling servant etc.
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
        callQueue.offer(request);
        queueEmptyCondition.signal();
        primaryQueueLock.unlock();
    }

    public void run ()
    {
        while (true) {
            primaryQueueLock.lock();
            this.dispatch();
            primaryQueueLock.unlock();
        }
    }

    private void tryExecuteRequest(MethodRequest methodRequest) {
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

    private void waitTillNotEmpty()
    {
        try {
            queueEmptyCondition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void dispatch()
    {
        if (priorityCallQueue.isEmpty()) {
            if (callQueue.isEmpty()) {
                waitTillNotEmpty();
            } else {
                tryExecuteRequest(callQueue.remove());
            }
        } else {
            if (priorityCallQueue.peek().guard(servant)) {
                priorityCallQueue.remove().call(servant);
            } else {
                if (callQueue.isEmpty()) {
                    waitTillNotEmpty();
                } else {
                    tryExecuteRequest(callQueue.remove());
                }
            }
        }
    }

}
