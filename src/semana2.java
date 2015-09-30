/**
 * Created by jorge on 30-09-2015.
 */

import javax.crypto.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class semana2 {


    public static void main(String[] args) {

        //final String RC4 = "RC4";
        Path foriginal = Paths.get("/home/jorge/Área de Trabalho/hellofriend", "foriginal.txt");
        Path fencriptado = Paths.get("/home/jorge/Área de Trabalho/hellofriend", "fencriptado.txt");
        Path ffinal = Paths.get("/home/jorge/Área de Trabalho/hellofriend", "ffinal.txt");

        KeyGenerator kg = null; // cria instancia do gerador de chaves
        try {
            kg = KeyGenerator.getInstance("RC4");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kg.init(128); // inicializa gerador de chaves (128 bits) e gera chave
        SecretKey key = kg.generateKey();

        encFile(key, foriginal, fencriptado);//encripta o ficheiro
        decFile(key, fencriptado, ffinal);

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
}