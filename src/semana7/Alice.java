package semana7;
/**
 * Created by jorge on 04-11-2015.
 */
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class Alice extends Thread {

    public static BigInteger P = new BigInteger("99494096650139337106186933977618513974146274831566768179581759037259788798151499814653951492724365471316253651463342255785311748602922458795201382445323499931625451272600173180136123245441204133515800495917242011863558721723303661523372572477211620144038809673692512025566673746993593384600667047373692203583");
    public static BigInteger G = new BigInteger("44157404837960328768872680677686802650999163226766694797650810379076416463147265401084491113667624054557335394761604876882446924929840681990106974314935015501571333024773172440352475358750668213444607353872754650805031912866692119819377041901642732455911509867728218394542745330014071040326856846990119719675");
    public static BigInteger a = new BigInteger(P.bitLength(),new SecureRandom());

    private int ct;
    protected Socket s;
    static final String UNSAFE_PASSWORD = "Password!!!";
    static final String CIPHER_MODE = "AES/CTR/NoPadding" ;

    Alice(Socket s, int c) {
            ct = c;
            this.s=s;
        }

        public void run() {
            try {

                String test;
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

                BigInteger A = G.modPow(a,P); //Calcula o A
                oos.writeObject(A); //envia o A para o Bob

                BigInteger B = (BigInteger)ois.readObject(); //recebe o B do Bob
                BigInteger salice = B.modPow(a,P); // calcula s a partir do B recebido do Bob

                System.out.println(salice);

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
                    //System.out.println(ct + ":" + new String());
                    System.out.println("["+ct + "]");

                } finally {
                   if (ois!=null) ois.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
