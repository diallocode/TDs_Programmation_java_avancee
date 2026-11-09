package td4;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class Ex02 {
    interface Parking {
        void arrive() throws InterruptedException;
        void depart();
    }
    static final class CarPark implements Parking {
        private final int initialCapacity;
        private int capacity;
        CarPark(int n) { initialCapacity = capacity = n; }
        public synchronized void arrive() throws InterruptedException {
            while (capacity == 0) wait();
            capacity--;
        }
        public synchronized void depart() {
            if (capacity == initialCapacity) throw new IllegalStateException();
            capacity++; notifyAll();
        }
    }
    static final class SemaphoreCarPark implements Parking {
        private final Semaphore places;
        SemaphoreCarPark(int n) { places = new Semaphore(n, true); }
        public void arrive() throws InterruptedException { places.acquire(); }
        public void depart() { places.release(); }
    }
    static final class Cars extends Thread {
        final Parking parking;
        final int unit;
        final AtomicInteger inside, maximum, completed;
        Cars(Parking p, int unit, AtomicInteger in, AtomicInteger max, AtomicInteger done) {
            parking = p; this.unit = unit; inside = in; maximum = max; completed = done;
        }
        public void run() {
            boolean entered = false;
            try {
                parking.arrive(); entered = true;
                int n = inside.incrementAndGet(); maximum.accumulateAndGet(n, Math::max);
                if (n > 4) throw new AssertionError("Parking surchargé");
                Thread.sleep(ThreadLocalRandom.current().nextInt(21) * (long) unit);
                completed.incrementAndGet();
            } catch (InterruptedException e) { interrupt(); }
            finally {
                if (entered) { inside.decrementAndGet(); parking.depart(); }
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        int unit = args.length > 0 && args[0].equals("--reel") ? 1000 : 1;
        for (Parking p : List.of(new CarPark(4), new SemaphoreCarPark(4))) {
            AtomicInteger in = new AtomicInteger(), max = new AtomicInteger(), done = new AtomicInteger();
            List<Thread> cars = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Thread t = new Cars(p, unit, in, max, done); cars.add(t); t.start();
                Thread.sleep(ThreadLocalRandom.current().nextInt(6) * (long) unit);
            }
            for (Thread t : cars) t.join();
            if (done.get() != 100 || in.get() != 0 || max.get() > 4) throw new AssertionError();
            System.out.printf("%s : terminées=%d, occupation max=%d/4, présentes=%d, unité=%d ms%n",
                    p.getClass().getSimpleName(), done.get(), max.get(), in.get(), unit);
        }
    }
}
