package exercice_8;

public class Requete{
	private int id;
	private String operation;
	private double value;
	
	public Requete(int id, String op, double val) {
		this.id = id;
		this.operation = op;
		this.value = val;
	}
	
	public synchronized String getOperation() {
		return operation;
	}
	
	public synchronized double getValue() {
		return value;
	}
	
	public synchronized int getId() {
		return id;
	}
}
