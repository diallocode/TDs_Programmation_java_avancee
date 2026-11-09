package td4;

import java.util.*;
import java.util.concurrent.*;

public final class Ex06 {
    static final List<String> FILES = List.of("a.txt", "b.txt", "c.txt", "d.txt", "e.txt");
    static final class Sauvegarde implements Runnable {
        final List<String> fichiers;
        final boolean original, calcul;
        Sauvegarde(List<String> fichiers, boolean original, boolean calcul) {
            this.fichiers = List.copyOf(fichiers); this.original = original; this.calcul = calcul;
        }
        public void run() {
            for (String f : fichiers) {
                if (!original && Thread.currentThread().isInterrupted()) {
                    System.out.println("Sauvegarde annulée"); return;
                }
                try {
                    System.out.println("Copie de " + f);
                    if (calcul) {
                        long start = System.nanoTime();
                        while (System.nanoTime() - start < 500_000_000L) Thread.onSpinWait();
                    } else Thread.sleep(500);
                } catch (InterruptedException e) {
                    if (!original) {
                        Thread.currentThread().interrupt();
                        System.out.println("Sauvegarde annulée"); return;
                    }
                }
            }
            if (!original && Thread.currentThread().isInterrupted()) {
                System.out.println("Sauvegarde annulée"); return;
            }
            System.out.println("Sauvegarde terminée");
        }
    }
    public static void main(String[] args) throws Exception {
        String mode = args.length == 0 ? "corrige" : args[0];
        Sauvegarde task = new Sauvegarde(FILES, mode.equals("original"), mode.equals("calcul"));
        if (mode.equals("executor")) {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                Future<?> f = pool.submit(task);
                Thread.sleep(1200);
                System.out.println("cancel(true)=" + f.cancel(true));
            } finally {
                pool.shutdown();
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) pool.shutdownNow();
            }
        } else {
            Thread t = new Thread(task, "sauvegarde"); t.start();
            Thread.sleep(1200); t.interrupt(); t.join();
        }
        System.out.println("Fin du programme");
    }
}
