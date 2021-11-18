import activeObject.Future;
import activeObject.resource.IntegerResource;
import activeObject.resource.Resource;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Producer implements Runnable {
    private final String name;
    private final ClientParameters parameters;
    private final Random generator;
    private double calculated;
    private int iterator;

    Producer(String name, ClientParameters parameters) {
        this.name = name;
        this.parameters = parameters;
        generator = new Random();
        calculated = 123;
        iterator = 0;
    }

    @Override
    public void run() {
        for (int x = 0; x < parameters.operationsBetween; x++) {
            List<Resource> production = new LinkedList<>();
            int productionSize = parameters.minConsumption + generator.nextInt(parameters.maxConsumption - parameters.minConsumption + 1);

            for (int i = 0; i < productionSize; i++) {
                production.add(new IntegerResource(iterator++));
            }

            Future<Void> prodSuccess = parameters.storage.put(production);

            calculations();

            prodSuccess.get();
            System.out.println(name + " produced " + production);
        }

        System.out.println(name + " calculated " + calculated);
    }

    private void calculations() {
        for (int i = 0; i < parameters.operationsBetween; i++) {
            calculated = Math.sin(calculated);
        }
    }
}