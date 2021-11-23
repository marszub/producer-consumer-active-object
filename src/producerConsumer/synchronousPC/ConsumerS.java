package producerConsumer.synchronousPC;

import producerConsumer.ClientParameters;
import producerConsumer.activeObject.Future;
import producerConsumer.activeObject.Proxy;
import producerConsumer.activeObject.resource.Resource;

import java.util.List;
import java.util.Random;

public class ConsumerS implements Runnable {
    private final String name;
    private final Storage4Cond storage;
    private final ClientParameters parameters;
    private final Random generator;
    private double calculated;
    private List<Thread> others;
    public long calculationsCounter;
    public int accessCounter;

    public ConsumerS(String name, Storage4Cond storage, ClientParameters parameters) {
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
        consumeNext();

        while(accessCounter < parameters.requests || calculationsCounter < parameters.calculations) {
            if(accessCounter < parameters.requests){
                consumeNext();
                accessCounter++;
            }

            for(int i = 0; calculationsCounter < parameters.calculations && i <= parameters.calculations/parameters.requests; i++)
                calculations();

            if(Thread.interrupted())
                return;
        }

        interruptAll();
        System.err.println(name + " calculated " + calculated);
    }

    private List<Resource> consumeNext(){
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