package td5;

import jakarta.persistence.*;

public class DemoJpa {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("td5");
        try {
            int id;
            EntityManager em = emf.createEntityManager();
            try {
                EntityTransaction tx = em.getTransaction();
                try {
                    tx.begin();
                    Livre livre = new Livre("Germinal", "Zola");
                    em.persist(livre);
                    tx.commit();
                    id = livre.getId();
                } catch (RuntimeException erreur) {
                    if (tx.isActive()) tx.rollback();
                    throw erreur;
                }
            } finally {
                em.close();
            }

            EntityManager lecture = emf.createEntityManager();
            try {
                Livre livre = lecture.find(Livre.class, id);
                if (livre == null || !"Germinal".equals(livre.getTitre())
                        || !"Zola".equals(livre.getAuteur())) {
                    throw new AssertionError("Livre introuvable ou incorrect");
                }
                System.out.printf("id=%d | %s | %s%n", id, livre.getTitre(), livre.getAuteur());
            } finally {
                lecture.close();
            }
        } finally {
            emf.close();
        }
    }
}
