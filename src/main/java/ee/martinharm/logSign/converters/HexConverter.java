package ee.martinharm.logSign.converters;

public class HexConverter {
    /**
     * Hexadecimal representation of the input byte array
     * @param data a byte[] to convert to Hex characters
     * @return A String containing Hex characters
     * https://docs.aws.amazon.com/amazonglacier/latest/dev/checksum-calculations.html
     */
    public static String convert(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte aData : data) {
            String hex = Integer.toHexString(aData & 0xFF);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }
}
