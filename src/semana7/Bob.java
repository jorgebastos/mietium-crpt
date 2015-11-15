package semana7;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Created by jorge on 04-11-2015.
 */
public class Bob {
     static final String CIPHER_MODE = "AES/CTR/NoPadding" ;
     static final String UNSAFE_PASSWORD = "Password!!!";

    public static BigInteger P = new BigInteger("99494096650139337106186933977618513974146274831566768179581759037259788798151499814653951492724365471316253651463342255785311748602922458795201382445323499931625451272600173180136123245441204133515800495917242011863558721723303661523372572477211620144038809673692512025566673746993593384600667047373692203583");
     public static BigInteger G = new BigInteger("44157404837960328768872680677686802650999163226766694797650810379076416463147265401084491113667624054557335394761604876882446924929840681990106974314935015501571333024773172440352475358750668213444607353872754650805031912866692119819377041901642732455911509867728218394542745330014071040326856846990119719675");
     public static BigInteger b = new BigInteger(P.bitLength(),new SecureRandom());


     static public void main(String []args) {
            try {


                Socket s = new Socket("localhost",4567);

                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

                BigInteger B = G.modPow(b,P); //calcula B
                oos.writeObject(B);//envia para a Alice o B

                BigInteger A = (BigInteger) ois.readObject(); //recebe o A da Alice
                BigInteger sbob = A.modPow(b,P); // calcula s a partir do B recebido do Bob
                System.out.println(sbob);

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

