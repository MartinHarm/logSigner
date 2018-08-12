package ee.martinharm.logSign;

import com.guardtime.ksi.Signer;
import com.guardtime.ksi.SignerBuilder;
import com.guardtime.ksi.service.KSISigningClientServiceAdapter;
import com.guardtime.ksi.service.client.KSIServiceCredentials;
import com.guardtime.ksi.service.client.KSISigningClient;
import com.guardtime.ksi.service.client.ServiceCredentials;
import com.guardtime.ksi.service.client.http.CredentialsAwareHttpSettings;
import com.guardtime.ksi.service.http.simple.SimpleHttpSigningClient;
import ee.martinharm.logSign.calculators.SHA256TreeHashCalculator;
import ee.martinharm.logSign.converters.HexConverter;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class LogSignApplication {
    public static void main(String[] args) {
        File inputFile = new File("c://log.txt");
        try {
            //Hashing
            byte[] treeHash = SHA256TreeHashCalculator.calculateSHA256TreeHash(inputFile);
            //Signing
            signTreeTopHash(treeHash);
            //Output
            System.out.printf("%s\n", HexConverter.convert(treeHash));
        } catch (IOException ioe) {
            System.err.format("Exception when reading from file %s: %s", inputFile,
                    ioe.getMessage());
            System.exit(-1);
        }
    }

    private static void signTreeTopHash(byte[] treeHash) {
        ServiceCredentials credentials = new KSIServiceCredentials("loginId", "loginKey");
        KSISigningClient ksiSigningClient = new SimpleHttpSigningClient(new CredentialsAwareHttpSettings("https://guardtime.com/", credentials));
        Signer signer = new SignerBuilder().setSigningService(new KSISigningClientServiceAdapter(ksiSigningClient)).build();
        try {
            //FIXME TODO - Use correct credentials and service url
            //signer.sign(treeHash);
            signer.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


}