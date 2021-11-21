package producerConsumer;

import producerConsumer.activeObject.Proxy;
import producerConsumer.activeObject.Scheduler;
import producerConsumer.activeObject.Servant;
import producerConsumer.asynchronousPC.ConsumerA;
import producerConsumer.asynchronousPC.ProducerA;
import producerConsumer.synchronousPC.ConsumerS;
import producerConsumer.synchronousPC.ProducerS;
import producerConsumer.synchronousPC.Storage4Cond;

import java.io.*;
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
    private static long lastCalculations;

    public static void main(String[] args) throws IOException {
        File file = new File("err.txt");
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        System.setErr(ps);

        FileWriter fileWriter = new FileWriter("out_1-10_0-5.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("Requests, Calculations, Minimal portion, Maximal portion, Operations quantum, Number of threads, Solution, Storage size");

        int tests = 0;
        for(int i = 1; i <= 10; i++)
            for(int j = 1; j <= 5; j++){
                lastCalculations = 0;
                ClientParameters clientParameters = new ClientParameters(100000, 100000, 1, 100, j * 100);
                TestParameters testParameters = new TestParameters(i*2, TestParameters.SolutionVersion.Asynchronous, 1000);

                tests++;
                double time = runTest(testParameters, clientParameters) / 1000000000.0;
                System.out.println(tests*100/450 + "% " + TestParameters.SolutionVersion.Asynchronous + " " + time + " seconds");

                clientParameters = new ClientParameters(100000, lastCalculations, 1, 100, j * 100);
                printWriter.println(clientParameters + ", " + testParameters + ", " + time);




                testParameters = new TestParameters(i*2, TestParameters.SolutionVersion.Synchronous, 1000);

                tests++;
                time = runTest(testParameters, clientParameters) / 1000000000.0;
                System.out.println(tests*100/450 + "% " + TestParameters.SolutionVersion.Synchronous + " " + time + " seconds");
                printWriter.println(clientParameters + ", " + testParameters + ", " + time);
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

        for (ProducerA producer: producers) {
            lastCalculations += producer.calculationsCounter;
        }

        for (ConsumerA consumer: consumers) {
            lastCalculations += consumer.calculationsCounter;
        }

        lastCalculations /= producers.size() + consumers.size();


        long time = nanoTime() - start;

        scheduler.schedulerThread.interrupt();

        return time;
    }
}
