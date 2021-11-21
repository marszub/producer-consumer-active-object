package producerConsumer.synchronousPC;

import producerConsumer.ClientParameters;
import producerConsumer.activeObject.Future;
import producerConsumer.activeObject.Proxy;
import producerConsumer.activeObject.resource.IntegerResource;
import producerConsumer.activeObject.resource.Resource;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ProducerS implements Runnable {
    private final String name;
    private final Storage4Cond storage;
    private final ClientParameters parameters;
    private final Random generator;
    private double calculated;
    private List<Thread> others;
    public long calculationsCounter;
    public int accessCounter;


    public ProducerS(String name, Storage4Cond storage, ClientParameters parameters) {
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
        produceNext();
        while(accessCounter < parameters.requests || calculationsCounter < parameters.calculations) {
            if(accessCounter < parameters.requests){
                produceNext();
                accessCounter++;
            }

            if(calculationsCounter < parameters.calculations)
                calculations();

            if(Thread.interrupted())
                return;
        }

        interruptAll();
        System.err.println(name + " calculated " + calculated);
    }

    private void produceNext(){
        List<Resource> production = new LinkedList<>();
        int productionSize = parameters.minPortion + generator.nextInt(parameters.maxPortion - parameters.minPortion + 1);

        for (int i = 0; i < productionSize; i++) {
            production.add(new IntegerResource(0));
        }
        storage.put(production);
    }

    private void calculations() {
        for (int i = 0; i < parameters.operationsQuantum; i++) {
            calculated = Math.sin(calculated);
        }
        calculationsCounter++;
    }
}