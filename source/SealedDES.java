//    Nick Carey
//    Department of Computer Science
//    Johns Hopkins University

////////////////////////////////////////////////////////////////////////////////
//
//  class: SealedDES
//
//  SealedDES encapsulates the DES encryption and decryption of Strings 
//  into SealedObjects.  It represesnts keys as integers (for simplicity).
//  
//  The main function gives and example of how to:
//    (1) generate a random 24 bit key by starting with a zero valued
//          8 bytes (64 bit key) and then encoding a string with that key
//    (2) perform a brute force search for that key and exame the 
//          resulting output for a known portion of plaintext (in this
//          case "Hopkins".
//
//  Your assignment will be to parallelize this process.
//
////////////////////////////////////////////////////////////////////////////////

import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

import java.util.Random;

import java.io.PrintStream;


class SealedDES implements Runnable {
    // Cipher for the class
    Cipher des_cipher;
	
    // Variable controlling debug print statements
    private final static int DEBUG = 1;

    // Key for the class
    SecretKeySpec the_key = null;

    // Byte arrays that hold key block
    byte[] deskeyIN = new byte[8];
    byte[] deskeyOUT = new byte[8];
		
    //ITS CHRISTMAS TIME OPEN UP YOUR PRESENTS
    //oh my goodness I have my very own SealedObject!
    // (I have my own copy so my brothers and sisters wont fight me for it)
    SealedObject mySealedObj;
    // Starting key value to test. run() will test keys starting in the range 
    // of myStartKey(inclusive) to myEndKey (exclusive)
    long myStartKey;
    long myEndKey;
    // ThreadID, for bookeeping and output
    int myThreadNum;
    // Execution runtime starttime, for bookeeping and output
    long runstart;
    // known plaintext to search decryption output for
    String searchPlaintext;

    // Constructor: initialize the cipher. Dont use this constructor
    public SealedDES () {
        try {
            des_cipher = Cipher.getInstance("DES");
        } catch ( Exception e )	{
	    System.out.println("Failed to create cipher.  Exception: " + e.toString() + " Message: " + e.getMessage()) ; 
        }
    }

    /* Constructor used to initialize object with a copy of everything it
    needs to start decrypting.  This is so SealedDES can run in its own thread
    with no dependencies on data needed by other threads. */
    //startKey is inclusive, endKey is exclusive. Together they denote a range of keys to test in run
    public SealedDES (SealedObject mySealed, long startKey, long endKey, int threadNum, long runstartIn, String plaintext) {

        this();
        mySealedObj = mySealed;
        myStartKey = startKey;
        myEndKey = endKey;
        myThreadNum = threadNum;
        runstart = runstartIn;
        searchPlaintext = plaintext;
 
    }

    /**
     *  Runnable Interface implementation - will test keys in range of myStartKey and myEndKey  
     *  on mySealedObj - will output progress reports every so often - Tests keys by looking
     *  for some known plaintext in the possibly decrypted output - in this case, "Hopkins"
     *  
     */
    public void run() {
        // create object to printf to the console
        PrintStream p = new PrintStream(System.out);
        // Search for the right key
        for ( long i = myStartKey; i < myEndKey; i++ ) {
            // Set the key and decipher the object
            this.setKey ( i );
            String decryptstr = this.decrypt ( mySealedObj );
		
            // Does the object contain the known plaintext
            if (( decryptstr != null ) && ( decryptstr.indexOf ( searchPlaintext ) != -1 )) {
                //  Remote printlns if running for time.
                p.printf("Found decrypt key %016x producing message: %s\n", i , decryptstr);
            }
		
            // Update progress every once in awhile.
            //  Remote printlns if running for time.
            if ( i % 100000 == 0 && DEBUG == 1) { 
                long elapsed = System.currentTimeMillis() - runstart;
                System.out.println ( "Thread " + myThreadNum + " searched key number " + i + " at " + elapsed + " milliseconds.");
            }
        }
    }


    // Decrypt the SealedObject
    //
    //   arguments: SealedObject that holds on encrypted String
    //   returns: plaintext String or null if a decryption error
    //     This function will often return null when using an incorrect key.
    //
    public String decrypt ( SealedObject cipherObj ) {
        try {
            return (String)cipherObj.getObject(the_key);
        } catch ( Exception e ) {
	    //do nothing :)
        }
        return null;
    }
	
    // Encrypt the message
    //
    //  arguments: a String to be encrypted
    //  returns: a SealedObject containing the encrypted string
    //
    public SealedObject encrypt ( String plainstr ) {
        try {
            des_cipher.init ( Cipher.ENCRYPT_MODE, the_key );
            return new SealedObject( plainstr, des_cipher );
        }catch ( Exception e ) {
            System.out.println("Failed to encrypt message. " + plainstr + ". Exception: " + e.toString() + ". Message: " + e.getMessage()) ; 
        }
        return null;
    }
	
    // Encrypt the message
    // This method is a copy of the one above but is useful for returning mass copies for parallel execution
    //  arguments: a String to be encrypted, num of copies in array
    //  returns: an array of SealedObject copies containing the encrypted string
    public SealedObject[] encryptArr ( String plainstr, int copies ) {
        try {
            des_cipher.init ( Cipher.ENCRYPT_MODE, the_key );
            SealedObject[] arr = new SealedObject[copies];
            for(int i = 0; i < copies; i++) {
                arr[i] = new SealedObject( plainstr, des_cipher );
            }
            return arr;
        } catch ( Exception e )	{
            System.out.println("Failed to encrypt message. " + plainstr + ". Exception: " + e.toString() + ". Message: " + e.getMessage()) ; 
        }
        return null;
    }

    //  Build a DES formatted key
    //
    //  Convert an array of 7 bytes into an array of 8 bytes.
    //
    private static void makeDESKey(byte[] in, byte[] out) {
        out[0] = (byte) ((in[0] >> 1) & 0xff);
        out[1] = (byte) ((((in[0] & 0x01) << 6) | (((in[1] & 0xff)>>2) & 0xff)) & 0xff);
        out[2] = (byte) ((((in[1] & 0x03) << 5) | (((in[2] & 0xff)>>3) & 0xff)) & 0xff);
        out[3] = (byte) ((((in[2] & 0x07) << 4) | (((in[3] & 0xff)>>4) & 0xff)) & 0xff);
        out[4] = (byte) ((((in[3] & 0x0F) << 3) | (((in[4] & 0xff)>>5) & 0xff)) & 0xff);
        out[5] = (byte) ((((in[4] & 0x1F) << 2) | (((in[5] & 0xff)>>6) & 0xff)) & 0xff);
        out[6] = (byte) ((((in[5] & 0x3F) << 1) | (((in[6] & 0xff)>>7) & 0xff)) & 0xff);
        out[7] = (byte) (   in[6] & 0x7F);
		
        for (int i = 0; i < 8; i++) {
            out[i] = (byte) (out[i] << 1);
        }
    }

    // Set the key (convert from a long integer)
    public void setKey ( long theKey ) {
        try {
            // convert the integer to the 8 bytes required of keys
            deskeyIN[0] = (byte) (theKey        & 0xFF );
            deskeyIN[1] = (byte)((theKey >>  8) & 0xFF );
            deskeyIN[2] = (byte)((theKey >> 16) & 0xFF );
            deskeyIN[3] = (byte)((theKey >> 24) & 0xFF );
            deskeyIN[4] = (byte)((theKey >> 32) & 0xFF );
            deskeyIN[5] = (byte)((theKey >> 40) & 0xFF );
            deskeyIN[6] = (byte)((theKey >> 48) & 0xFF );

            // theKey should never be larger than 56-bits, so this should always be 0
            deskeyIN[7] = (byte)((theKey >> 56) & 0xFF );
			
            // turn the 56-bits into a proper 64-bit DES key
            makeDESKey(deskeyIN, deskeyOUT);
			
            // Create the specific key for DES
            the_key = new SecretKeySpec ( deskeyOUT, "DES" );
        } catch ( Exception e )	{
            System.out.println("Failed to assign key" +  theKey + ". Exception: " + e.toString() + ". Message: " + e.getMessage()) ;
        }
    }
	
	
    // Program demonstrating how to create a random key and then search for the key value.
    public static void main ( String[] args ) {
        if ( 2 != args.length ) {
            System.out.println ("Usage: java SealedDES #threads key_size_in_bits " + args.length);
            return;
        }
        // Get the arguments
        int numThreads = Integer.parseInt( args[0] );
        long keybits = Long.parseLong ( args[1] );

        long maxkey = ~(0L);
        maxkey = maxkey >>> (64 - keybits);

        System.out.println("Number of keys to search: " + (maxkey + 1));		

        // Create a simple cipher
        SealedDES enccipher = new SealedDES ();
		
        // Get a number between 0 and 2^64 - 1
        Random generator = new Random ();
        long key =  generator.nextLong();
		
        // Mask off the high bits so we get a short key
        key = key & maxkey;
		
        // Set up a key
        enccipher.setKey ( key ); 
		
        // Generate a sample string
        String plainstr = "Johns Hopkins afraid of the big bad wolf?";
		
        // Encrypt and get a copy of SealedObject for each thread		
	SealedObject[] sldObjArr = enccipher.encryptArr(plainstr, numThreads);

        // Here ends the set-up.  Pretending like we know nothing except sldObj,
        // discover what key was used to encrypt the message.
		
	// Get and store the current time -- for timing
        long runstart = System.currentTimeMillis();
        // calculate interval of keys for each thread to test
        long interval = maxkey / numThreads;
        long startint = 0;		
        long endint = 0;
   
        //plaintext hint we are searching for
        String hint = "Hopkins";

        Thread[] threads = new Thread[numThreads];
        for(int m = 0; m < numThreads; m++){
            startint = endint;
            endint += interval;
            if(m == numThreads - 1) {
                endint = maxkey;
            }
            threads[m] = new Thread( new SealedDES(sldObjArr[m], startint, endint, m, runstart, hint ));
            threads[m].start();
        }		

        for(int m = 0; m < numThreads; m++) {
            try{
                threads[m].join();
            } catch (InterruptedException e) {
                System.out.println("Thread " + m + " interrupted.  Exception: " + e.toString() + " Message: " + e.getMessage());
                return;
            }
        }

        // Output search time
        long elapsed = System.currentTimeMillis() - runstart;
        long keys = maxkey + 1;
        System.out.println ( "Completed search of " + keys + " keys at " + elapsed + " milliseconds.");
    }
}

