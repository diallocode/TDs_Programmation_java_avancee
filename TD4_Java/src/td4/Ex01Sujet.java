package td4;

/** Version demandée, conservant l'algorithme de sémaphore du PDF. */
public final class Ex01Sujet {
    static final class Semaphore {
        private int permits;
        Semaphore(int initialPermits){permits=initialPermits;}
        synchronized void acquire(){
            permits=permits-1;
            while(permits<0){
                try{wait();}catch(InterruptedException e){ /* version du sujet */ }
            }
        }
        synchronized void release(){permits++;if(permits<=0)notify();}
    }
    static class LetterThread extends Thread {
        final Semaphore mine,next;final char letter;
        LetterThread(char letter,Semaphore mine,Semaphore next){this.letter=letter;this.mine=mine;this.next=next;}
        public void run(){for(int i=0;i<5;i++){mine.acquire();System.out.print(letter);next.release();}}
    }
    static final class ThreadA extends LetterThread{ThreadA(Semaphore a,Semaphore c){super('A',a,c);}}
    static final class ThreadB extends LetterThread{ThreadB(Semaphore b,Semaphore a){super('B',b,a);}}
    static final class ThreadC extends LetterThread{ThreadC(Semaphore c,Semaphore b){super('C',c,b);}}
    public static void main(String[] args)throws InterruptedException{
        Semaphore a=new Semaphore(1),b=new Semaphore(0),c=new Semaphore(0);
        Thread[] ts={new ThreadA(a,c),new ThreadB(b,a),new ThreadC(c,b)};
        for(Thread t:ts)t.start();for(Thread t:ts)t.join();System.out.println();
    }
}
