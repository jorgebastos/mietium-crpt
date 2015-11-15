package semana8;

/**
 * Created by jorge on 13-11-2015.
 */
import java.net.*;
import java.io.*;

public class nAlice extends Thread {
    private int ct;
    protected Socket s;

    nAlice(Socket s, int c) {
        ct = c;
        this.s=s;
    }

    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            String test;
            try {
                while (true) {
                    test = (String) ois.readObject();
                    System.out.println(ct + " : " + test);
                }
            } catch (EOFException e) {
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
