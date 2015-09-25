import java.io.IOException;

class Mycat {
    public static void main(String[] args) throws IOException {
        byte[] buffer = new byte[1000];
        while(true) {
            int bRead = System.in.read(buffer);
            if (bRead == -1) {
                return;
            }
            System.out.write(buffer, 0, bRead);
        }
    }
}
