package td4;

/** Alternance A -> C -> B, cinq fois. */
public final class Ex01 {
    static final class Semaphore {
        private int permits;
        Semaphore(int initialPermits) {
            if (initialPermits < 0) throw new IllegalArgumentException();
            permits = initialPermits;
        }
        synchronized void acquire() throws InterruptedException {
            while (permits == 0) wait();
            permits--;
        }
        synchronized void release() { permits++; notifyAll(); }
    }
    static class LetterThread extends Thread {
        private final Semaphore mine, next;
        private final char letter;
        LetterThread(char letter, Semaphore mine, Semaphore next) {
            super("Thread-" + letter);
            this.letter = letter; this.mine = mine; this.next = next;
        }
        public void run() {
            try {
                for (int i = 0; i < 5; i++) {
                    mine.acquire();
                    System.out.print(letter);
                    next.release();
                }
            } catch (InterruptedException e) { interrupt(); }
        }
    }
    static final class ThreadA extends LetterThread {
        ThreadA(Semaphore a, Semaphore c) { super('A', a, c); }
    }
    static final class ThreadB extends LetterThread {
        ThreadB(Semaphore b, Semaphore a) { super('B', b, a); }
    }
    static final class ThreadC extends LetterThread {
        ThreadC(Semaphore c, Semaphore b) { super('C', c, b); }
    }
    public static void main(String[] args) throws InterruptedException {
        Semaphore a = new Semaphore(1), b = new Semaphore(0), c = new Semaphore(0);
        Thread[] threads = {new ThreadA(a, c), new ThreadB(b, a), new ThreadC(c, b)};
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        System.out.println();
    }
}
