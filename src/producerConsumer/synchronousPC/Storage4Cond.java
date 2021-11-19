package producerConsumer.synchronousPC;

import producerConsumer.activeObject.resource.Resource;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Storage4Cond{
    public final ReentrantLock lock;
    private final Condition taken;
    private final Condition firstTaken;
    private final Condition put;
    private final Condition firstPut;
    private final List<Resource> slots;
    private final int size;

    private boolean waitingForStoring;
    private boolean waitingForTaking;

    public Storage4Cond(int size){
        this.size = size;
        slots = new LinkedList<>();
        lock = new ReentrantLock(true);
        taken = lock.newCondition();
        firstTaken = lock.newCondition();
        put = lock.newCondition();
        firstPut = lock.newCondition();

        waitingForTaking = false;
        waitingForStoring = false;
    }

    public void put(List<Resource> toStore){
        lock.lock();
        try{
            while(waitingForStoring){
                taken.await();
            }

            waitingForStoring = true;

            while(slots.size() + toStore.size() > size){
                firstTaken.await();
            }

            slots.addAll(toStore);
            taken.signal();
            firstPut.signal();

            waitingForStoring = false;
        } catch (InterruptedException e) {
            lock.unlock();
        } finally{
            lock.unlock();
        }
    }

    public List<Resource> get(int howMany){
        List<Resource> toTake = new LinkedList<>();
        lock.lock();
        try{
            while(waitingForTaking){
                put.await();
            }

            waitingForTaking = true;

            while(slots.size() - howMany < 0){
                firstPut.await();
            }

            toTake = new LinkedList<>(slots.subList(0, howMany));
            slots.removeAll(toTake);
            put.signal();
            firstTaken.signal();

            waitingForTaking = false;
        } catch (InterruptedException e) {
            lock.unlock();
        } finally{
            lock.unlock();
        }
        return toTake;
    }
}
