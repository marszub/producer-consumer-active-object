package producerConsumer;

import producerConsumer.activeObject.Proxy;
import producerConsumer.activeObject.Scheduler;
import producerConsumer.activeObject.Servant;
import producerConsumer.asynchronousPC.ConsumerA;
import producerConsumer.asynchronousPC.ProducerA;
import producerConsumer.synchronousPC.ConsumerS;
import producerConsumer.synchronousPC.ProducerS;
import producerConsumer.synchronousPC.Storage4Cond;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.nanoTime;

final class TestParameters{
    public final int threadNum;
    public final SolutionVersion version;
    public final int servantSize;

    public enum SolutionVersion{Synchronous, Asynchronous};

    TestParameters(int threadNum, SolutionVersion version, int servantSize) {
        this.threadNum = threadNum;
        this.version = version;
        this.servantSize = servantSize;
    }

    public String toString(){
        return threadNum + ", " + version + ", " + servantSize;
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        FileWriter fileWriter = new FileWriter("out.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (TestParameters.SolutionVersion solution:
             TestParameters.SolutionVersion.values())
            for(int i = 1; i <= 30; i++)
                for(int j = 1; j <= 30; j++) {
                    ClientParameters clientParameters = new ClientParameters(100000, 100000, 1, 100, j*10);
                    TestParameters testParameters = new TestParameters(i * 5, solution, 1000);

                    printWriter.println(clientParameters + ", " + testParameters + ", " + runTest(testParameters, clientParameters) / 1000000000.0);
                }
        printWriter.close();
        System.out.println("Success");
    }

    private static long runTest(TestParameters testParameters, ClientParameters clientParameters){
        switch (testParameters.version){
            case Synchronous:
                return synchronousTest(testParameters, clientParameters);
            case Asynchronous:
                return asynchronousTest(testParameters, clientParameters);
        }
        return 0;
    }

    private static long synchronousTest(TestParameters testParameters, ClientParameters clientParameters){
        Storage4Cond storage = new Storage4Cond(testParameters.servantSize);

        //Initialize threads

        List<Thread> threads = new LinkedList<>();
        List<ProducerS> producers = new LinkedList<>();
        List<ConsumerS> consumers = new LinkedList<>();
        for(int i = 0; i < testParameters.threadNum; i++) {
            ProducerS producer = new ProducerS("P" + i, storage, clientParameters);
            producers.add(producer);
            threads.add(new Thread(producer, "P" + i));
        }

        for(int i = 0; i < testParameters.threadNum; i++) {
            ConsumerS consumer = new ConsumerS("C" + i, storage, clientParameters);
            consumers.add(consumer);
            threads.add(new Thread(consumer, "C" + i));
        }

        for (ProducerS producer: producers) {
            producer.setOtherThreads(threads);
        }

        for (ConsumerS consumer: consumers) {
            consumer.setOtherThreads(threads);
        }

        long start = nanoTime();

        //Start threads
        for (Thread thread: threads) {
            thread.start();
        }

        //Join threads
        for (Thread thread: threads) {
            try {
                thread.join();
            }catch (InterruptedException e){
                System.out.println("Interrupted");
            }
        }

        return nanoTime() - start;
    }

    private static long asynchronousTest(TestParameters testParameters, ClientParameters clientParameters){
        //Initialize threads
        Scheduler scheduler = new Scheduler(new Servant(testParameters.servantSize));
        Proxy storage = new Proxy(scheduler);

        List<Thread> threads = new LinkedList<>();
        List<ProducerA> producers = new LinkedList<>();
        List<ConsumerA> consumers = new LinkedList<>();
        for(int i = 0; i < testParameters.threadNum; i++) {
            ProducerA producer = new ProducerA("P" + i, storage, clientParameters);
            producers.add(producer);
            threads.add(new Thread(producer, "P" + i));
        }

        for(int i = 0; i < testParameters.threadNum; i++) {
            ConsumerA consumer = new ConsumerA("C" + i, storage, clientParameters);
            consumers.add(consumer);
            threads.add(new Thread(consumer, "C" + i));
        }

        for (ProducerA producer: producers) {
            producer.setOtherThreads(threads);
        }

        for (ConsumerA consumer: consumers) {
            consumer.setOtherThreads(threads);
        }

        long start = nanoTime();

        //Start threads
        for (Thread thread: threads) {
            thread.start();
        }

        //Join threads
        for (Thread thread: threads) {
            try {
                thread.join();
            }catch (InterruptedException e){
                System.out.println("Interrupted");
            }
        }

        long time = nanoTime() - start;

        scheduler.schedulerThread.interrupt();

        return time;
    }
}
