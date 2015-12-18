package semana12;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertPath;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import static semana12.Servidors12.validateCert;

/**
 * Created by jorge on 13-12-2015.
 */
public class Bobs12 {
    static final String CIPHER_MODE = "AES/CTR/NoPadding";
    static final String SIGNATURE = "SHA1withDSA";
    static KeyStore keystore;
    static public void main(String[] args) throws Exception {
        try {


            Socket s = new Socket("localhost", 4567);

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());


            FileInputStream fis = new FileInputStream("Cliente.p12");
            keystore.getInstance("PKCS12");
            keystore.load(fis,"1234".toCharArray());

            PrivateKey bobprivkey = (PrivateKey) keystore.getKey("Cliente1", "1234".toCharArray());

            Signature sign = Signature.getInstance(SIGNATURE);
            sign.initSign(bobprivkey);

            Certificate[] certArray = (Certificate[]) keystore.getCertificateChain("Cliente1");
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            CertPath certPath = certFactory.generateCertPath(Arrays.asList((java.security.cert.Certificate[]) certArray));


            PublicKey a_PK = (PublicKey) ois.readObject();//recebe a publica da Alice
            DHParameterSpec dhSpec = ((DHPublicKey) a_PK).getParams();//fica com os parametros DH associados a chave publica da alice

            KeyPairGenerator b_kpg = KeyPairGenerator.getInstance("DiffieHellman");
            b_kpg.initialize(dhSpec);//cria gerador com mesmo parametros que alice

            KeyPair b_kp = b_kpg.generateKeyPair();
            PublicKey b_PK = b_kp.getPublic();
            oos.writeObject(b_PK);//envia a publica para a alice

            sign.update(a_PK.getEncoded());
            sign.update(b_PK.getEncoded());

            byte[] bobsign = sign.sign();
            oos.writeObject(bobsign); //envia a assinatura dele à alice e o certificado
            oos.writeObject(certPath);

            byte[] alicesign = (byte[]) ois.readObject(); //recebe a assinatura da alice e o certificado
            CertPath alicecert = (CertPath) ois.readObject();


            //TODO: Verificar o certificado!!!
            validateCert(alicecert);

            //le chave publica da licinha do certificado
            X509Certificate ac = (X509Certificate) alicecert.getCertificates().get(0);
            PublicKey alicepubkey = ac.getPublicKey();

            sign.initVerify(alicepubkey);//verifica a assinatura
            sign.update(a_PK.getEncoded());
            sign.update(b_PK.getEncoded());

            //TODO: verificar o nome que está no certificado para garantir autenticidade

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
