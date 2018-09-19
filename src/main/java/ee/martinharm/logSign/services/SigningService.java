package ee.martinharm.logSign.services;

import com.guardtime.ksi.service.client.http.CredentialsAwareHttpSettings;
import com.guardtime.ksi.service.http.simple.SimpleHttpSigningClient;
import com.guardtime.ksi.service.KSISigningClientServiceAdapter;
import com.guardtime.ksi.service.client.KSIServiceCredentials;
import com.guardtime.ksi.service.client.ServiceCredentials;
import com.guardtime.ksi.service.client.KSISigningClient;
import com.guardtime.ksi.SignerBuilder;
import com.guardtime.ksi.Signer;

public class SigningService {
    public static void signHash(byte[] topHash) {
        ServiceCredentials credentials = new KSIServiceCredentials("loginId", "loginKey");
        KSISigningClient ksiSigningClient = new SimpleHttpSigningClient(new CredentialsAwareHttpSettings("https://guardtime.com/", credentials));
        Signer signer = new SignerBuilder().setSigningService(new KSISigningClientServiceAdapter(ksiSigningClient)).build();
        try {
            //FIXME TODO - Use correct credentials and service url
            //signer.sign(topHash);
            signer.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
