package semana12;

import javax.crypto.spec.DHParameterSpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.Collections;


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

    public static void validateCert(CertPath cp) throws FileNotFoundException, CertificateException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, CertPathValidatorException {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate cacert = factory.generateCertificate(new FileInputStream("CA.cer"));
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
            TrustAnchor anchor = new TrustAnchor((X509Certificate) cacert, null);
            // TrustAnchor representa os pressupostos de confiança que se aceita como válidos
            // (neste caso, unicamente a CA que emitiu os certificados)
            PKIXParameters params = new PKIXParameters(Collections.singleton(anchor));
            // ...no nosso caso, vamos simplesmente desactivar a verificação das CRLs
            params.setRevocationEnabled(false);
            // Validacao
            CertPathValidatorResult cpvResult = cpv.validate(cp, params);
            try {
                cpvResult = cpv.validate(cp, params);
                System.out.println("SE CHEGOU AQUI, TUDO DEVE TER CORRIDO BEM!!!");
            } catch (InvalidAlgorithmParameterException iape) {
                System.err.println("Erro de validação: " + iape);
                System.exit(1);
            } catch (CertPathValidatorException cpve) {
                System.err.println("FALHA NA VALIDAÇÃO: " + cpve);
                System.err.println("Posição do certificado causador do erro: "
                        + cpve.getIndex());
            }
    }
}


