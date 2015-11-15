package semana8;

/**
 * Created by jorge on 13-11-2015.
 */
import java.net.*;
import java.io.*;
import java.security.AlgorithmParameterGenerator;

public class nBob {

    static public void main(String []args) {

        try {
            Socket s = new Socket("localhost",4567);

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

            AlgorithmParameterGenerator algDH = AlgorithmParameterGenerator.getInstance("DH");
            algDH.init(1024);

            String test;
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            while((test=stdIn.readLine())!=null) {
                oos.writeObject(test);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}