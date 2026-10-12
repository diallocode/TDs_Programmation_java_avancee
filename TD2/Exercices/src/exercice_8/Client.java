package exercice_8;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client implements Runnable {
    private Serveur serveur;
    private int clientId;
    private String operation;
    private double valeur;

    public Client(Serveur serveur, int clientId, String operation, double valeur) {
        this.serveur = serveur;
        this.clientId = clientId;
        this.operation = operation;
        this.valeur = valeur;
    }

    @Override
    public void run() {
        // Création de la requête avec l'opération et la valeur passées en paramètre
        Requete requete = new Requete(clientId, operation, valeur);
        
        // Envoi au serveur (qui utilise le Thread Pool)[cite: 3]
        Future<Resultat> futureResultat = serveur.recevoirRequete(requete);
        
        try {
            // Récupération bloquante du résultat asynchrone[cite: 3]
            Resultat resultat = futureResultat.get();
            System.out.println("Client " + clientId + " [" + operation + " " + valeur + "] -> " + resultat);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}