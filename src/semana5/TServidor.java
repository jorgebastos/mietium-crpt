package semana5;

/**
 * Created by jorge on 16-10-2015.
 */

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;

public class TServidor extends Thread {
    private int ct;
    protected Socket s;
    static final String UNSAFE_PASSWORD = "Password!!!";
    static final String CIPHER_MODE = "AES/CTR/PKCS5Padding" ;

    TServidor(Socket s, int c) {
        ct = c;
        this.s=s;
    }

    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte rawbits[] = sha256.digest(UNSAFE_PASSWORD.getBytes("UTF-8"));

            Cipher c = Cipher.getInstance(CIPHER_MODE);
            SecretKey key = new SecretKeySpec(rawbits,0,16,"AES");
            byte ivbits[] = (byte[]) ois.readObject();

            IvParameterSpec iv = new IvParameterSpec(ivbits);
            c.init(Cipher.DECRYPT_MODE, key, iv);

            Mac m = Mac.getInstance("Hmack-SHA-1");
            m.init(new SecretKeySpec(rawbits,16,16,"Hmack-SHA-1"));

            byte ciphertext[], cleartext[], mac[];
            String test;
            try {

                while (true) {
                    //test = (String) ois.readObject();
                    //System.out.println(ct + " : " + test);
                    ciphertext = (byte[]) ois.readObject();
                    mac= (byte[]) ois.readObject();
                    if (mac.equals(m.doFinal(ciphertext))) {
                        cleartext = c.update(ciphertext);
                        System.out.println(ct + " : " + new String(cleartext, "UTF-8"));
                    }
                    else{
                        System.out.println(ct + ": EROO NA VERIFICAÇÃO!!!");
                    }
                }
            } catch (EOFException e) {
                cleartext=c.doFinal();
                System.out.println(ct + ":" + new String());
                System.out.println("["+ct + "]");

            } finally {
                if (ois!=null) ois.close();
                if (oos!=null) oos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}