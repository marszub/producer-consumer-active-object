package producerConsumer.activeObject;

import producerConsumer.activeObject.methodRequest.MethodRequest;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler implements Runnable {
    private final Servant servant;
    public Thread schedulerThread;
    private final ActivationQueue activationQueue;
    private final Queue<MethodRequest> prioritizedQueue;

    public Scheduler(Servant servant) {
        this.servant = servant;
        activationQueue = new ActivationQueue();
        prioritizedQueue = new LinkedList<>();

        //starting Scheduler's thread
        schedulerThread = new Thread(this);
        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }

    public void enqueue(MethodRequest request) {
        activationQueue.enqueue(request);
    }

    public void run() {
        while (!Thread.interrupted())
            this.dispatch();
    }

    private void dispatch() {
        if (executePrioritizedRequest())
            return;
        processNextRequest();
    }

    private boolean executePrioritizedRequest() {
        if (!prioritizedQueue.isEmpty() && prioritizedQueue.peek().guard(servant)) {
            prioritizedQueue.remove().call(servant);
            return true;
        }
        return false;
    }

    private void processNextRequest() {
        MethodRequest methodRequest = activationQueue.dequeue();

        //if there are prioritized requests of the same type
        if (!prioritizedQueue.isEmpty() && prioritizedQueue.peek().getClass() == methodRequest.getClass()) {
            prioritizedQueue.add(methodRequest);
            return;
        }

        if (methodRequest.guard(servant)) {
            methodRequest.call(servant);
            return;
        }

        prioritizedQueue.add(methodRequest);
    }
}
