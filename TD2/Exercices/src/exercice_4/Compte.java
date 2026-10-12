package exercice_4;

public class Compte {
	private int id;
	private double solde;
	
	public Compte(int id, double s) {
		this.id = id;
		this.solde = s;
	}
	
	public synchronized void retirer(double m) {
		this.solde -= m;
	}
	
	
	public synchronized void ajouter(double m) {
		this.solde += m;
	}
	
	
	public synchronized double getSolde() {
		return this.solde;
	}
	
	public synchronized int getId() {
		return this.id;
	}
	
	
	public synchronized static void operation(Compte s, Compte d, double montant) {
		if(s.getId() == d.getId()) {
			return;
		}
	
		Compte premierVerrou = s.getId() < d.getId() ? s : d; 
		Compte secondVerrou = s.getId() < s.getId() ? d : s;
		
		synchronized(premierVerrou) {
			synchronized(secondVerrou) {
				if(s.getSolde() >= montant) {
					s.retirer(montant);
					d.ajouter(montant);
				}
			}
		}
		
	}
	
}
