package semana8;

/**
 * Created by jorge on 13-11-2015.
 */

import javax.crypto.spec.DHParameterSpec;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;

public class Servidors8 {

        static private int tcount;

        static public void main(String []args) {
            tcount = 0;
            try {
                ServerSocket ss = new ServerSocket(4567);

                AlgorithmParameterGenerator algenerator = AlgorithmParameterGenerator.getInstance("DH");
                algenerator.init(1024);
                AlgorithmParameters parameters = algenerator.generateParameters();
                DHParameterSpec dhSpec = parameters.getParameterSpec(DHParameterSpec.class);


                while(true) {
                    Socket s = ss.accept();
                    tcount++;
                    Alices8 tas8 = new Alices8(s,tcount,dhSpec);
                    tas8.start();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

