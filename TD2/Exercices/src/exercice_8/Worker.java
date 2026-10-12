package exercice_8;

import java.util.concurrent.Callable;

public class Worker implements Callable<Resultat> {
	private Requete r;
	
	public Worker(Requete r) {
		this.r = r;
	}
	

	/*
	public Resultat call() throws Exception {
		try {
			Resultat resultat = 0;
			switch(r.getOperation()) {
			case "SQUARE" -> resultat = new (r.getId()*r.getValue());
			case "ADD" -> resultat = r.getId()+r.getValue();
			case "SUB" -> resultat = -r.getId()+r.getValue();
			case "DIV" -> resultat = r.getValue()/r.getId();
			default -> throw new IllegalArgumentException("Opération inconnue : "+ r.getOperation());
			};
			return resultat;
			
		} catch (Exception e) {
			return new Resultat(r.getId(), e.getMessage());
		}
	}	*/
	
	@Override
	public Resultat call() throws Exception {
	    try {
	        // On calcule la valeur finale grâce au switch expression
	        double valeurFinale = switch (r.getOperation().toUpperCase()) {
	            case "SQUARE" -> r.getValue() * r.getValue();
	            case "ADD"    -> r.getId() + r.getValue();
	            case "SUB"    -> r.getValue() - r.getId(); // Ajustez l'ordre selon votre logique
	            case "DIV"    -> {
	                if (r.getId() == 0) {
	                    throw new ArithmeticException("Division par zéro (id=0)");
	                }
	                yield r.getValue() / r.getId(); // renvoyer la valeur dans un bloc switch
	            }
	            default -> throw new IllegalArgumentException("Opération inconnue : " + r.getOperation());
	        };
	        
	        // On retourne l'objet Resultat avec l'id et la valeur calculée
	        return new Resultat(r.getId(), valeurFinale);
	        
	    } catch (Exception e) {
	        return new Resultat(r.getId(), e.getMessage());
	    }
	}

}
