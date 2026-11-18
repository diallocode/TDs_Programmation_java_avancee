package td6;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Locale;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class ChiffrementDynamique {
    private enum Algorithme {
        AES("AES/GCM/NoPadding", 16, 12),
        DES("DES/CBC/PKCS5Padding", 8, 8);

        private final String transformation;
        private final int tailleCle;
        private final int tailleIv;

        Algorithme(String transformation, int tailleCle, int tailleIv) {
            this.transformation = transformation;
            this.tailleCle = tailleCle;
            this.tailleIv = tailleIv;
        }

        private AlgorithmParameterSpec parametres(byte[] iv) {
            return this == AES ? new GCMParameterSpec(128, iv) : new IvParameterSpec(iv);
        }
    }

    private ChiffrementDynamique() {}

    public static void main(String[] args) throws ReflectiveOperationException, GeneralSecurityException {
        try (Scanner entree = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Nom complet de la classe Cipher :");
            String nomClasse = entree.nextLine().strip();
            System.out.println("Algorithme (AES ou DES) :");
            Algorithme algorithme = Algorithme.valueOf(entree.nextLine().strip().toUpperCase(Locale.ROOT));
            System.out.println("Texte à chiffrer :");
            String texte = entree.nextLine();
            chiffrerEtDechiffrer(nomClasse, algorithme, texte);
        }
    }

    private static void chiffrerEtDechiffrer(String nomClasse, Algorithme algorithme, String texte)
            throws ReflectiveOperationException, GeneralSecurityException {
        if (!nomClasse.equals("javax.crypto.Cipher")) {
            throw new IllegalArgumentException("Classe attendue : javax.crypto.Cipher");
        }
        Class<?> classeCipher = Class.forName(nomClasse);
        Method getInstance = classeCipher.getMethod("getInstance", String.class);
        String transformation = algorithme.transformation;
        Cipher chiffreur = (Cipher) getInstance.invoke(null, transformation);
        Cipher dechiffreur = (Cipher) getInstance.invoke(null, transformation);

        SecureRandom alea = new SecureRandom();
        byte[] octetsCle = new byte[algorithme.tailleCle];
        byte[] iv = new byte[algorithme.tailleIv];
        alea.nextBytes(octetsCle);
        alea.nextBytes(iv);
        SecretKeySpec cle = new SecretKeySpec(octetsCle, algorithme.name());
        AlgorithmParameterSpec parametres = algorithme.parametres(iv);

        chiffreur.init(Cipher.ENCRYPT_MODE, cle, parametres);
        byte[] chiffre = chiffreur.doFinal(texte.getBytes(StandardCharsets.UTF_8));
        dechiffreur.init(Cipher.DECRYPT_MODE, cle, parametres);
        String dechiffre = new String(dechiffreur.doFinal(chiffre), StandardCharsets.UTF_8);

        Base64.Encoder base64 = Base64.getEncoder();
        System.out.println("Transformation : " + transformation);
        System.out.println("Fournisseur : " + chiffreur.getProvider().getName());
        System.out.println("Taille du matériau de clé : " + octetsCle.length + " octets");
        System.out.println("Texte clair : " + texte);
        System.out.println("IV (Base64) : " + base64.encodeToString(iv));
        System.out.println("Texte chiffré (Base64) : " + base64.encodeToString(chiffre));
        System.out.println("Texte déchiffré : " + dechiffre);
        System.out.println("Identique à l’original : " + texte.equals(dechiffre));
    }
}
