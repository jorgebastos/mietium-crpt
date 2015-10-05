/**
 * Created by jorge on 30-09-2015.
 */
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class semana2 {


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        /*
            Primeira versão. Não utilizava as funções de genkey e readkey
        Path foriginal = Paths.get("/home/jorge/Área de Trabalho/hellofriend/semana2", "foriginal.txt");
        Path fencriptado = Paths.get("/home/jorge/Área de Trabalho/hellofriend/semana2", "fencriptado.txt");
        Path ffinal = Paths.get("/home/jorge/Área de Trabalho/hellofriend/semana2", "ffinal.txt");

        KeyGenerator kg = null; // cria instancia do gerador de chaves
        try {
            kg = KeyGenerator.getInstance("RC4");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kg.init(128); // inicializa gerador de chaves (128 bits) e gera chave
        SecretKey key = kg.generateKey();

        encFile(key, foriginal, fencriptado);//encripta o ficheiro
        decFile(key, fencriptado, ffinal);*/
        SecretKey key;
        Path in_file;
        Path out_file;
        Path k_path;


        System.out.println("hello");
        if(args.length==2 || args.length==4)
        {
            if(args[0].equals("-genkey"))
            {
                k_path = Paths.get(args[1]);
                genKey(k_path);
            }else if (args.length == 4) {
                if (args[0].equals("-enc")) {
                    k_path = Paths.get(args[1]);
                    in_file = Paths.get(args[2]);
                    out_file = Paths.get(args[3]);
                    key = readKey(k_path);
                    encFile(key, in_file, out_file);
                }
                if (args[0].equals("-dec")) {
                    k_path = Paths.get(args[1]);
                    in_file = Paths.get(args[2]);
                    out_file = Paths.get(args[3]);
                    key = readKey(k_path);
                    decFile(key, in_file, out_file);
                }else{
                    System.out.println("How to use:");
                    System.out.println("prog -genkey <keyfile>");
                    System.out.println("prog -enc <keyfile> <infile> <outfile>");
                    System.out.println("prog -dec <keyfile> <infile> <outfile>");
                }


            }
        }
    }




    public static void encFile(SecretKey key, Path inputFile, Path outputFile) {
        byte[] inf;
        byte[] encf;

        try {
            inf = Files.readAllBytes(inputFile);
            Cipher e = Cipher.getInstance("RC4"); //cria instancia da cifra
            //inicializa cifra
            e.init(Cipher.ENCRYPT_MODE, key);
            encf = e.doFinal(inf); // guarda em encf o array encriptado
            Files.write(outputFile, encf);

        } catch (IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

    }

    public static void decFile(SecretKey key, Path inputFile, Path outputFile) {
        byte[] inf;
        byte[] ffile;

        try {
            inf = Files.readAllBytes(inputFile);
            Cipher e = Cipher.getInstance("RC4"); //cria instancia da cifra
            //inicializa cifra
            e.init(Cipher.DECRYPT_MODE, key);
            ffile = e.doFinal(inf);// guarda em ffile a mensagem original já desencriptada
            Files.write(outputFile, ffile);
        } catch (IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }


    }

    public static void genKey(Path kPath) throws IOException, NoSuchAlgorithmException {
        byte[] fkey;
        KeyGenerator kg; // cria instancia do gerador de chaves
        kg = KeyGenerator.getInstance("RC4");
        kg.init(128);
        SecretKey key = kg.generateKey();
        fkey = key.getEncoded();
        Files.write(kPath, fkey);

    }

    public static SecretKey readKey(Path kPath) throws IOException {
        byte[] fkey; //128 bits
        fkey= Files.readAllBytes(kPath);
        SecretKey fk = new SecretKeySpec(fkey,"RC4");
        return fk;

    }

}

