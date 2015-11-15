package semana7;



import java.net.ServerSocket;
import java.net.Socket;


public class Servidor {

        static private int tcount;

        static public void main(String []args) {
            tcount = 0;
            try {
                ServerSocket ss = new ServerSocket(4567);


                while(true) {
                    Socket s = ss.accept();
                    tcount++;
                    Alice talice = new Alice(s,tcount);
                    talice.start();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

