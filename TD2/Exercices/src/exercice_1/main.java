package exercice_1;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Affiche a = new Affiche();
		Thread t = new Thread(a);
		
		Affiche b = new Affiche();
		Thread t1 = new Thread(b);
		
		t.start();
		
		t1.start();
	}

}
