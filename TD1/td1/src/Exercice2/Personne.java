package Exercice2;

public class Personne {
	
	protected String name;
	
	public Personne(String name) {
		this.name = name;
	}
	
	public void afficher() {
		System.out.println("Je suis " + this.name);
	}

}
