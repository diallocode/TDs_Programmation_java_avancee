package Exercice6;

public class Main {

	public static void main(String[] args) {
		int[] tab = new int[20]; 
        int[] elementsInitiaux = {5, 9, 20, 21, 22, 24, 26, 35, 36, 37};
        
        for (int i = 0; i < elementsInitiaux.length; i++) {
            tab[i] = elementsInitiaux[i];
        }
		
        int taille = elementsInitiaux.length;
        
		EntiersTrier En = new EntiersTrier(tab,taille);
		
		En.inserer(1);
		En.inserer(3);
		En.inserer(4);
		
		En.afficher();
		System.out.println("On insere 2");
		En.inserer(2);
		En.afficher();
		
		System.out.println("On insere 0");
		En.inserer(0);
		En.afficher();
	}

}
