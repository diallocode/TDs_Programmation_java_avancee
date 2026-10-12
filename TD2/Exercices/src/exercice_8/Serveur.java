package exercice_8;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Serveur {
	ExecutorService pool = Executors.newFixedThreadPool(100);
	
	public Future<Resultat> recevoirRequete(Requete requete) {
        Worker worker = new Worker(requete);
        
        Future<Resultat> futureResultat = pool.submit(worker);
        
        return futureResultat;
        }

    
    public void arreterServeur() {
        pool.shutdown();
    }
	
	
}
