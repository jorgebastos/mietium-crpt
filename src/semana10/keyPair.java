package semana10;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by jorge on 20-11-2015.
 */


public class keyPair { //gerar  os pares de chaves.
    public static final String ALGORITHM = "RSA";

    static Path pubkeypath;
    static Path privkeypath;

    public keyPair(Path publickeypath, Path privatekeypath) {
        privkeypath = privatekeypath;
        pubkeypath = publickeypath;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {


        if (args.length == 2) {

            pubkeypath = Paths.get(args[0]);
            privkeypath = Paths.get(args[1]);

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            KeyPair key = keyGen.generateKeyPair();

            PublicKey pubkey = key.getPublic();
            PrivateKey privkey = key.getPrivate();

            byte[] publickey = pubkey.getEncoded();
            byte[] privatekey = privkey.getEncoded();

            Files.write(pubkeypath, publickey);
            Files.write(privkeypath, privatekey);

            System.out.println("Chaves criadas com sucesso");
        }
        else{
            System.out.println("How to use:");
            System.out.println("prog <pubkey_path> <privkey_path>");
        }
    }

    public static PublicKey readPublicKey(String filePath) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        File pkey = new File(filePath);

        FileInputStream fis = new FileInputStream(pkey);
        DataInputStream dis = new DataInputStream(fis);
        byte[] kb = new byte[(int)pkey.length()];
        dis.readFully(kb);
        dis.close();

        X509EncodedKeySpec keyspec = new X509EncodedKeySpec(kb);
        KeyFactory keyfactory = KeyFactory.getInstance("RSA");
        return keyfactory.generatePublic(keyspec);
    }

    public static PrivateKey readPrivateKey(String filePath) throws Exception {

        File pkey = new File(filePath);

        FileInputStream fis = new FileInputStream(pkey);
        DataInputStream dis = new DataInputStream(fis);
        byte[] kb = new byte[(int)pkey.length()];
        dis.readFully(kb);
        dis.close();

        PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(kb);
        KeyFactory keyfactory = KeyFactory.getInstance("RSA");
        return keyfactory.generatePrivate(keyspec);
    }
}
