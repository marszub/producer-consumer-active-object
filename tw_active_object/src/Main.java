import activeObject.Proxy;
import activeObject.Scheduler;
import activeObject.Servant;

import java.util.concurrent.Future;


public class Main {
    Future i;

    public static void main(String[] args) {
        Proxy storage = new Proxy(new Scheduler(new Servant(40)));
        ClientParameters parameters = new ClientParameters(storage, 10000000, 1, 10, 100000);

        Thread p1 = new Thread(new Producer("P1", parameters));
        Thread p2 = new Thread(new Producer("P2", parameters));
        Thread p3 = new Thread(new Producer("P3", parameters));
        Thread c1 = new Thread(new Consumer("C1", parameters));
        Thread c2 = new Thread(new Consumer("C2", parameters));
        Thread c3 = new Thread(new Consumer("C3", parameters));

        p1.start();
        p2.start();
        p3.start();
        c1.start();
        c2.start();
        c3.start();
        try {
            p1.join();
            p2.join();
            p3.join();
            c1.join();
            c2.join();
            c3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
