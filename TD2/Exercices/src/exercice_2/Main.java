package exercice_2;

public class Main {

	public static void main(String[] args) {
		// Creation de l'instance de la classe Compte
		Compte c = new Compte(500);
		
		// Affichage du solde
		c.afficherSolde();
		
		Mouvement m1 = new Mouvement(c,100);
		Mouvement m2 = new Mouvement(c,-50);
		
		Thread t1 = new Thread(m1, "Thread-Depot");
		Thread t2 = new Thread(m2, "Thread-Retrait");
		
		t1.start();
		t2.start();
		
		// Attente 
		try {
			t1.join();
			t2.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
				
        System.out.println("----------------------------------------");
        System.out.println("Solde final du compte : " + c.getSolde());
	}

}
