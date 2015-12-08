package semana10;

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
import java.security.*;

/**
 * Created by jorge on 27-11-2015.
 */
public class Bobs10 {
    static final String CIPHER_MODE = "AES/CTR/NoPadding";
    static final String SIGNATURE = "SHA1withDSA";

    static public void main(String[] args) throws Exception {
        try {


            Socket s = new Socket("localhost", 4567);

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());


            PublicKey a_PK = (PublicKey) ois.readObject();//recebe a publica da Alice
            DHParameterSpec dhSpec = ((DHPublicKey) a_PK).getParams();//fica com os parametros DH associados a chave publica da alice

            KeyPairGenerator b_kpg = KeyPairGenerator.getInstance("DiffieHellman");
            b_kpg.initialize(dhSpec);//cria gerador com mesmo parametros que alice

            KeyPair b_kp = b_kpg.generateKeyPair();
            PublicKey b_PK = b_kp.getPublic();
            oos.writeObject(b_PK);//envia a publica para a alice

            //Ler as respectivas chaves dos ficheiros.
            System.out.print("Alice public key file:");
            String alicepubfile = System.console().readLine();
            System.out.print("Bob private key file:");
            String bobprivfile = System.console().readLine();

            PublicKey alicepubkey = keyPair.readPublicKey(alicepubfile);
            PrivateKey bobprivkey = keyPair.readPrivateKey(bobprivfile);

            Signature sign = Signature.getInstance(SIGNATURE);
            sign.initSign(bobprivkey);

            sign.update(a_PK.getEncoded());
            sign.update(b_PK.getEncoded());

            byte[] bobsign = sign.sign();
            oos.writeObject(bobsign); //envia a assinatura dele à alice

            byte[] alicesign = (byte[]) ois.readObject(); //recebe a assinatura da alice

            sign.initVerify(alicepubkey);//verifica a a
            sign.update(a_PK.getEncoded());
            sign.update(b_PK.getEncoded());

            if (sign.verify(alicesign)) {

                KeyAgreement b_kagree = KeyAgreement.getInstance("DH");
                b_kagree.init(b_kp.getPrivate());


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

                Mac m = Mac.getInstance("Hmac-SHA-1");
                m.init(new SecretKeySpec(rawbits, 16, 16, "Hmac-SHA-1"));

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
                }
                oos.write(c.doFinal());
            } else {
                System.out.println("assinatura nao verificada");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
