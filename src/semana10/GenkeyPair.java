package semana10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;

/**
 * Created by jorge on 20-11-2015.
 */

public class GenkeyPair { //gerar  os pares de chaves.

    public static final String ALGORITHM = "RSA";
    static Path privkeypath;
    static Path pubkeypath;

    public GenkeyPair(Path publickeypath, Path privatekeypath){
        privkeypath = privatekeypath;
        pubkeypath = publickeypath;
    }
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();

        PublicKey pubkey = key.getPublic();
        PrivateKey privkey = key.getPrivate();

        byte[] publickey = pubkey.getEncoded();
        byte[] privatekey = privkey.getEncoded();

        Files.write(pubkeypath,publickey);
        Files.write(privkeypath,privatekey);

    }
}
