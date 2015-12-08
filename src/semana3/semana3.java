package semana3;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by jorge on 02-10-2015.
 */
public class semana3 { //faltam testes finais


    public static void main(String[] args) throws Exception { //TODO
        SecretKey key;
        Path in_file;
        Path out_file;
        String k_path;
        String k_password;
        KeyGenerator kg;


        if (args.length == 3 || args.length == 5) {
            if (args[0].equals("-genkey")) {
                byte[] fkey;
                k_path = args[1];
                k_password = args[2];
                KeyStore.ProtectionParameter passwordProtection = new KeyStore.PasswordProtection(k_password.toCharArray());

                kg = genKey();
                key = kg.generateKey();
                fkey = key.getEncoded();

                KeyStore keyStore = createKS(k_path, k_password);
                KeyStore.SecretKeyEntry skentry = new KeyStore.SecretKeyEntry(key);
                keyStore.setEntry("keystore", skentry, passwordProtection);
                keyStore.store(new FileOutputStream(k_path), k_password.toCharArray());


            } else if (args.length == 5) {
                if (args[0].equals("-enc")) {
                    k_path = args[1];
                    k_password = args[2];
                    in_file = Paths.get(args[3]);
                    out_file = Paths.get(args[4]);
                    KeyStore.ProtectionParameter passwordProtection = new KeyStore.PasswordProtection(k_password.toCharArray());
                    KeyStore keyStore = createKS(k_path, k_password);
                    KeyStore.Entry entry = keyStore.getEntry("keystore",passwordProtection);
                    key = ((KeyStore.SecretKeyEntry) entry).getSecretKey();

                    //inicializa mac
                    Mac mac = Mac.getInstance("Hmac-SHA-1");
                    mac.init(key);

                    //vector de inicializacao
                    SecureRandom r = new SecureRandom();
                    byte[] iv = new byte[16];
                    r.nextBytes(iv);
                    IvParameterSpec ivSpec=new IvParameterSpec(iv);
                    encFile(key, in_file, out_file, ivSpec,mac);
                }
                if (args[0].equals("-dec")) {
                    k_path = args[1];
                    k_password = args[2];
                    in_file = Paths.get(args[3]);
                    out_file = Paths.get(args[4]);
                    KeyStore.ProtectionParameter passwordProtection = new KeyStore.PasswordProtection(k_password.toCharArray());
                    KeyStore keyStore = createKS(k_path, k_password);
                    KeyStore.Entry entry = keyStore.getEntry("keystore",passwordProtection);
                    key = ((KeyStore.SecretKeyEntry) entry).getSecretKey();

                    //inicializa mac
                    Mac mac = Mac.getInstance("Hmac-SHA-1");
                    mac.init(key);

                    //vector de inicializacao
                    SecureRandom r = new SecureRandom();
                    byte[] iv = new byte[16];
                    r.nextBytes(iv);
                    IvParameterSpec ivSpec=new IvParameterSpec(iv);

                    decFile(key, in_file, out_file, ivSpec,mac);

                } else {
                    System.out.println("How to use:");
                    System.out.println("prog -genkey <keyfile> <password>");
                    System.out.println("prog -enc <keyfile> <password> <infile> <outfile>");
                    System.out.println("prog -dec <keyfile> <password> <infile> <outfile>");
                }


            }
        }
    }



    public static void encFile(SecretKey key, Path inputFile, Path outputFile, IvParameterSpec ivspec, Mac mac) throws Exception{
        byte[] inf;
        byte[] encf;

        try {
            inf = Files.readAllBytes(inputFile);
            Cipher e = Cipher.getInstance("AES/CBC/PKCS5Padding"); //cria instancia da cifra
            //inicializa cifra
            e.init(Cipher.ENCRYPT_MODE, key, ivspec);
            encf = e.doFinal(inf); // guarda em encf o array encriptado
            byte[] last =mac.doFinal(encf);
            Files.write(outputFile, last);

        } catch (IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

    }


    public static void decFile(SecretKey key, Path inputFile, Path outputFile, IvParameterSpec ivspec, Mac mac) throws InvalidAlgorithmParameterException {
        byte[] inf;
        byte[] ffile;

        try {
            inf = Files.readAllBytes(inputFile);
            Cipher e = Cipher.getInstance("AES/CBC/PKCS5Padding"); //cria instancia da cifra
            //inicializa cifra
            e.init(Cipher.DECRYPT_MODE, key, ivspec);
            ffile = e.doFinal(inf);// guarda em ffile a mensagem original já desencriptada
            byte[] last = mac.doFinal(ffile);
            Files.write(outputFile, last);
        } catch (IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }


    }


    public static KeyGenerator genKey() throws IOException, NoSuchAlgorithmException {
        KeyGenerator kg; // cria instancia do gerador de chaves
        kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        return kg;
    }


    public static KeyStore createKS(String fkey, String pass) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        File file = new File(fkey);
        KeyStore ks = KeyStore.getInstance("JCEKS");
        if (file.exists()) {
            ks.load(new FileInputStream(file), pass.toCharArray());
        } else {
            ks.load(null, null);
            ks.store(new FileOutputStream(fkey), pass.toCharArray());
        }
        return ks;
    }
}









