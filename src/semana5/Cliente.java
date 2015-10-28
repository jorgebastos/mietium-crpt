package semana5;

/**
 * Created by jorge on 16-10-2015.
 */

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;

public class Cliente {
    static final String CIPHER_MODE = "AES/CTR/PKCS5Padding" ;
    static final String UNSAFE_PASSWORD = "Password!!!";

    static public void main(String []args) {
        try {
            Socket s = new Socket("localhost", 4567);

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte rawbits[] = sha256.digest(UNSAFE_PASSWORD.getBytes("UTF-8"));

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
