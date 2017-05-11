package co.umbc.cmsc.hopin;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class that does a lot of utility stuff for other classes, it's methods may not be related to each other.
 *
 * Created by crypton on 5/1/17.
 */

public class UtilHelper {


    public static final int DELAY_FIRST_REFRESH = 1000; // amount of time in milliseconds before first execution.
    public static final int REFRESH_RATE = 15000; // refresh every 15 seconds

    public static String appendEmailSuffix( String username ) {
        String emailSuffix = "@umbc.edu";
        return new StringBuilder().append(username).append(emailSuffix).toString();
    }

    public static String sha1Hash( String toHash )
    {
        String hash = null;
        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formatting()
            hash = bytesToHex( bytes );
        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return hash;
    }

    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex( byte[] bytes ) {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }

} // end class