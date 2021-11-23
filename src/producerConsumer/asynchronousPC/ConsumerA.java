package producerConsumer.asynchronousPC;

import producerConsumer.ClientParameters;
import producerConsumer.activeObject.Future;
import producerConsumer.activeObject.Proxy;
import producerConsumer.activeObject.resource.Resource;

import java.util.List;
import java.util.Random;

public class ConsumerA implements Runnable {
    private final String name;
    private final Proxy storage;
    private final ClientParameters parameters;
    private final Random generator;
    private double calculated;
    private List<Thread> others;
    public long calculationsCounter;
    public int accessCounter;

    public ConsumerA(String name, Proxy storage, ClientParameters parameters) {
        this.name = name;
        this.storage = storage;
        this.parameters = parameters;
        generator = new Random();
        calculated = 123;
        calculationsCounter = 0;
        accessCounter = 0;
    }

    public void setOtherThreads(List<Thread> threads){
        others = threads;
    }

    private void interruptAll(){
        for (Thread thread: others) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {
        Future<List<Resource>> consumption = consumeNext();

        while(accessCounter < parameters.requests || calculationsCounter < parameters.calculations) {
            if(accessCounter < parameters.requests && consumption.isReady()){
                consumption.get();
                consumption = consumeNext();
                accessCounter++;
            }

            calculations();

            if(Thread.interrupted())
                return;
        }

        interruptAll();
        System.err.println(name + " calculated " + calculated);
    }

    private Future<List<Resource>> consumeNext(){
        int consumptionSize = parameters.minPortion + generator.nextInt(parameters.maxPortion - parameters.minPortion + 1);
        return storage.get(consumptionSize);
    }

    private void calculations() {
        for (int i = 0; i < parameters.operationsQuantum; i++) {
            calculated = Math.sin(1 + calculated);
        }
        calculationsCounter++;
    }
}