package semana8;

/**
 * Created by jorge on 13-11-2015.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Bobs8 {

    static public void main(String []args) {

        try {
            Socket s = new Socket("localhost",4567);

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());


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