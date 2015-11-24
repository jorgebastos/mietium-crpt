package semana8;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PublicKey;

/**
 * Created by jorge on 13-11-2015.
 */
public class Bobs8 {

    static final String CIPHER_MODE = "AES/CTR/NoPadding" ;

    static public void main(String []args) {
        try {


            Socket s = new Socket("localhost",4567);

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

            PublicKey a_PK = (PublicKey)ois.readObject();//recebe a publica da Alice
            DHParameterSpec dhSpec = ((DHPublicKey)a_PK).getParams();//fica com os parametros DH associados a chave publica da alice

            KeyPairGenerator b_kpg = KeyPairGenerator.getInstance("DiffieHellman");
            b_kpg.initialize(dhSpec);//cria gerador com mesmo parametros que alice

            KeyPair b_kp = b_kpg.generateKeyPair();

            KeyAgreement b_kagree = KeyAgreement.getInstance("DH");
            b_kagree.init(b_kp.getPrivate());

            PublicKey b_PK = b_kp.getPublic();
            oos.writeObject(b_PK);

            //executa o acordo de chaves
            b_kagree.doPhase(a_PK, true);

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte rawbits[] = sha256.digest(b_kagree.generateSecret());

            Cipher c = Cipher.getInstance(CIPHER_MODE);
            SecretKey key = new SecretKeySpec(rawbits, 0, 16, "AES");
            //IvParameterSpec iv = new IvParameterSpec(rawbits,16,16);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte iv[] = c.getIV();

            oos.writeObject(iv);

            Mac m= Mac.getInstance("Hmac-SHA-1");
            m.init(new SecretKeySpec(rawbits,16,16,"Hmac-SHA-1"));

            String test;
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            byte ciphertext[], mac[];
            while ((test = stdIn.readLine()) != null) {
                ciphertext = c.update(test.getBytes("UTF-8"));
                if (ciphertext != null) {
                    mac = m.doFinal(ciphertext);
                    oos.writeObject(ciphertext);
                    oos.writeObject(mac);
                }
            } oos.write(c.doFinal());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}