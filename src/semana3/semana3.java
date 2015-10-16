package semana3;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by jorge on 02-10-2015.
 */
public class semana3 {

    //private static char[] password = "password".toCharArray();

    public static void main(String[] args ) throws IOException, NoSuchAlgorithmException { //TODO
        SecretKey key;
        Path in_file;
        Path out_file;
        Path k_path;


        System.out.println("hello");
        if(args.length==2 || args.length==4)
        {
            if(args[0].equals("-genkey"))
            {
                k_path = Paths.get(args[1]);
                genKey(k_path);
            }else if (args.length == 4) {
                if (args[0].equals("-enc")) {
                    k_path = Paths.get(args[1]);
                    in_file = Paths.get(args[2]);
                    out_file = Paths.get(args[3]);
                    key = readKey(k_path);
                    encFile(key, in_file, out_file);
                }
                if (args[0].equals("-dec")) {
                    k_path = Paths.get(args[1]);
                    in_file = Paths.get(args[2]);
                    out_file = Paths.get(args[3]);
                    key = readKey(k_path);
                    decFile(key, in_file, out_file);
                }else{
                    System.out.println("How to use:");
                    System.out.println("prog -genkey <keyfile>");
                    System.out.println("prog -enc <keyfile> <infile> <outfile>");
                    System.out.println("prog -dec <keyfile> <infile> <outfile>");
                }


            }
        }
    }



    public static void encFile(SecretKey key, Path inputFile, Path outputFile) {
        byte[] inf;
        byte[] encf;

        try {
            inf = Files.readAllBytes(inputFile);
            Cipher e = Cipher.getInstance("RC4"); //cria instancia da cifra
            //inicializa cifra
            e.init(Cipher.ENCRYPT_MODE, key);
            encf = e.doFinal(inf); // guarda em encf o array encriptado
            Files.write(outputFile, encf);

        } catch (IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

    }

    public static void decFile(SecretKey key, Path inputFile, Path outputFile) {
        byte[] inf;
        byte[] ffile;

        try {
            inf = Files.readAllBytes(inputFile);
            Cipher e = Cipher.getInstance("RC4"); //cria instancia da cifra
            //inicializa cifra
            e.init(Cipher.DECRYPT_MODE, key);
            ffile = e.doFinal(inf);// guarda em ffile a mensagem original já desencriptada
            Files.write(outputFile, ffile);
        } catch (IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }


    }

    public static void genKey(Path kPath) throws IOException, NoSuchAlgorithmException {
        byte[] fkey;
        KeyGenerator kg; // cria instancia do gerador de chaves
        kg = KeyGenerator.getInstance("RC4");
        kg.init(128);
        SecretKey key = kg.generateKey();
        fkey = key.getEncoded();
        Files.write(kPath, fkey);

    }
        //TODO
    public static SecretKey readKey(Path kPath) throws IOException {
        byte[] fkey; //128 bits
        fkey= Files.readAllBytes(kPath);
        SecretKey fk = new SecretKeySpec(fkey,"RC4");
        return fk;

    }


    public static KeyStore createKS(String fkey, String pass ) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        File file = new File(fkey);
        KeyStore ks = KeyStore.getInstance("JCEKS");
        if(file.exists()){
            ks.load(new FileInputStream(file), pass.toCharArray());
        }
        else{
            ks.load(null,null);
        }
        return ks;
    }

    public static void storeKS(KeyStore ks, String outfile, SecretKey skey, String pass ) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore.SecretKeyEntry skEntry =
                new KeyStore.SecretKeyEntry(skey);
        char[] myPassword = pass.toCharArray();
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(myPassword);
        ks.setEntry("SecretKeyAlias", skEntry, protParam);
        ks.store(new FileOutputStream(outfile),myPassword);

    }

    public static SecretKey loadKey(String infile,String pass) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
        char[] keyPass = pass.toCharArray();
        KeyStore ks = KeyStore.getInstance("JCEKS");
        ks.load(new FileInputStream(infile),keyPass);
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyPass);

        KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry)
                ks.getEntry("SecretKeyAlias", protParam);
        SecretKey myKey = skEntry.getSecretKey();

        return myKey;
    }

}









