package Exercice2;

public class Forgeron extends Personne {

	public Forgeron(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override 
	public void afficher() {
		System.out.println("Je suis " + name + " le forgeron");
	}


}
