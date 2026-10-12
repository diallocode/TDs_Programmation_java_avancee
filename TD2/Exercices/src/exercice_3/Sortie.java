package exercice_3;
import java.util.Random;

public class Sortie implements Runnable {
	private Compteur c;
	
	public Sortie(Compteur c) {
		this.c = c;
	}
	
	@Override
	public void run() {
		Random r = new Random();
		int n = r.nextInt(10);	// Aleatoire de passant
		c.decremente(n);
		
	}

}
