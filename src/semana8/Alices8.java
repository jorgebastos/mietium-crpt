package semana8;

/**
 * Created by jorge on 13-11-2015.
 */

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PublicKey;

public class Alices8 extends Thread {
    private int ct;
    protected Socket s;
    private static DHParameterSpec dhSpec;
    static final String CIPHER_MODE = "AES/CTR/NoPadding" ;

    Alices8(Socket s, int c, DHParameterSpec dhSpec) {
        ct = c;
        this.s=s;
        this.dhSpec=dhSpec;
    }

    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());


            //cria e inicializa o gerador de par de chaves DH
            KeyPairGenerator a_kpg = KeyPairGenerator.getInstance("DH");
            a_kpg.initialize(dhSpec);

            //cria o par de chaves
            KeyPair a_kp = a_kpg.generateKeyPair();
            //cria acordo de chaves
            KeyAgreement a_kagree = KeyAgreement.getInstance("DH");
            a_kagree.init(a_kp.getPrivate()); //incia com chave privada


            PublicKey a_PK = a_kp.getPublic();
            oos.writeObject(a_PK); //envia ao Bob a sua chave publica

            PublicKey b_PK = (PublicKey)ois.readObject();//recebe a publica do Bob

            a_kagree.doPhase(b_PK,true);


            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte rawbits[] = sha256.digest(a_kagree.generateSecret()); //gerar segredo

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