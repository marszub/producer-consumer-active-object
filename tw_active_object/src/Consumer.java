import activeObject.Future;
import activeObject.resource.Resource;

import java.util.List;
import java.util.Random;

class Consumer implements Runnable {
    private final String name;
    private final ClientParameters parameters;
    private final Random generator;
    private double calculated;

    Consumer(String name, ClientParameters parameters) {
        this.name = name;
        this.parameters = parameters;
        generator = new Random();
        calculated = 123;
    }

    @Override
    public void run() {
        for (int x = 0; x < parameters.operationsBetween; x++) {

            int consumptionSize = parameters.minConsumption + generator.nextInt(parameters.maxConsumption - parameters.minConsumption + 1);
            Future<List<Resource>> consumption = parameters.storage.get(consumptionSize);

            calculations();

            System.out.println(name + " took " + consumption.get().size());
        }

        System.out.println(name + " calculated " + calculated);
    }

    private void calculations() {
        for (int i = 0; i < parameters.operationsBetween; i++) {
            calculated = Math.sin(calculated);
        }
    }
}