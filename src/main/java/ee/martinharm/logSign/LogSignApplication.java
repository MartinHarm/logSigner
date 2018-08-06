package ee.martinharm.logSign;

import com.guardtime.ksi.hashing.DataHasher;
import com.guardtime.ksi.hashing.HashAlgorithm;

public class LogSignApplication {

    public static void main(String[] args) {

        DataHasher dataHasher = new DataHasher(HashAlgorithm.SHA2_256);
        dataHasher.addData("1".getBytes());
        System.out.println(dataHasher.getHash());

    }
}
