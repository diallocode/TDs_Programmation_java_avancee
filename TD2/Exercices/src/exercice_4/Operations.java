jpackage exercice_4;
import java.util.Random;

public class Operations implements Runnable {
	private Compte[] comptes;
	private int nbTransfers;
	
	
	public Operations(Compte[] A, int n){
		this.comptes = A;
		this.nbTransfers = n;
		
	}
	
	@Override
	public void run() {
		int i = 0;
		while(i < nbTransfers) {
			Random r = new Random();
			int indexA = r.nextInt(100);
			int indexB = r.nextInt(100);
			
			if(indexA != indexB) {
				double montant = 1 + r.nextInt(1000);	// montant aleatoire a transferer     
				Compte.operation(comptes[indexA], comptes[indexB], montant);
			}
			i++;
		}
		
	} 
		
		

}
