package exercice_4;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int nbComptes = 100;
		double soldeInitial = 1000;
		
		Compte[] compte = new Compte[nbComptes];
		
		for(int i = 0; i<nbComptes; i++) {
			compte[i] = new Compte(i,soldeInitial);
		}
		
		int nbTransfert = 100000;
		int nbThreads = 20;
		int n = nbTransfert/nbThreads;
		Operations o = new Operations(compte,n);
		
		// Threads.
		Thread[] t = new Thread[nbThreads];
		for(int i = 0; i<nbThreads; i++) {
			t[i] = new Thread(o, "Thread-" + i);
		}
		
		// Start
		for(int i=0; i<nbThreads; i++) {
			t[i].start();
		}
		
		//
		for(int i=0; i<nbThreads; i++) {
			try {
				t[i].join(); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		double solde_f = 0;
		for(int i=0; i<nbComptes; i++) {
			double solde = compte[i].getSolde();
			solde_f += solde;
		}
		
		
		double sommeTotale = 0;
        boolean soldeNegatif = false;

        for (int i = 0; i < nbComptes; i++) {
            double soldeActuel = compte[i].getSolde();
            sommeTotale += soldeActuel;
            if (soldeActuel < 0) {
                soldeNegatif = true;
                System.out.println("ALERTE : Le compte " + compte[i].getId() + " est négatif (" + soldeActuel + ")");
            }
        }

        System.out.println("=== RÉSULTATS DE LA SIMULATION ===");
        System.out.println("Somme totale des soldes : " + sommeTotale);
        System.out.println("Présence de compte négatif : " + (soldeNegatif ? "Oui" : "Non"));
	}

}
