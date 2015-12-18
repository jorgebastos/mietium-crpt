package semana12;

import javax.crypto.spec.DHParameterSpec;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;


/**
 * Created by jorge on 10-12-2015.
 */

public class Servidors12 {

    static private int tcount;

    static public void main(String[] args) {
        tcount = 0;
        try {

            AlgorithmParameterGenerator algenerator = AlgorithmParameterGenerator.getInstance("DH");
            algenerator.init(1024);
            AlgorithmParameters parameters = algenerator.generateParameters();
            DHParameterSpec dhSpec = parameters.getParameterSpec(DHParameterSpec.class);
            FileInputStream fis = new FileInputStream("Servidor.p12");
            ServerSocket ss = new ServerSocket(4567);

            while (true) {
                Socket s = ss.accept();
                tcount++;
                Alices12 tas12 = new Alices12(s, tcount, dhSpec);
                tas12.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


