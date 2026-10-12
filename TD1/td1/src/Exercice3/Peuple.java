package Exercice3;

public class Peuple {
	private int nbHommes = 0;
	private Homme[] pays = new Homme[100];
	
	public void naissance(Homme h) {
		pays[nbHommes] = h;
		nbHommes++;
	}
	 
	
	public void explorer() {
		for(int i = 0; i<nbHommes; i++) {
			pays[i].identite();
		}
	}
}
