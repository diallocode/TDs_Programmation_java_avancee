package exercice_8;

public class Main {
    public static void main(String[] args) {
        // Le serveur initialise automatiquement son Thread Pool (ex: 100 threads max)[cite: 3]
        Serveur serveur = new Serveur();

        // Tableau de tests simulant différents clients avec des opérations variées
        // Rappel : ID du client sert aussi de diviseur dans le cas de "DIV" (valeur / id)
        String[] operations = {"SQUARE", "ADD", "SUB", "DIV", "UNKNOWN"};
        double[] valeurs = {25.0, 10.0, 50.0, 100.0, 10.0};

        int nombreClients = 6;
        Thread[] threadsClients = new Thread[nombreClients];

        for (int i = 0; i < nombreClients; i++) {
            // On fait tourner les opérations pour les tests
            String op = operations[i % operations.length];
            double val = valeurs[i % valeurs.length];
            int idClient = i; // Attention si id=0 pour la division, ça testera l'erreur de division par zéro !

            Client client = new Client(serveur, idClient, op, val);
            threadsClients[i] = new Thread(client);
            threadsClients[i].start();
        }

        // Attente de la fin de tous les clients (via join)[cite: 3]
        for (int i = 0; i < nombreClients; i++) {
            try {
                threadsClients[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Arrêt propre du serveur[cite: 3]
        serveur.arreterServeur();
        System.out.println("\n Tous les tests d'opérations sont terminés.");
    }
}