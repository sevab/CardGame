package cardgame;

import java.io.BufferedReader;
// import java.io.BufferedWriter;
import java.io.File;
// import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.nio.channels.FileChannel;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author sevabaskin
 */
public class Helper {
    static int linesInAFile(File file) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }

    static void appendLineToFile(File file, String line) throws IOException {
    	// test: should create file if doesn't exist
    	// test: should_append_line

        // try {
        //     BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        //     bw.write(line+"\n");
        //     bw.close();
        // } catch (IOException e) {}
        try {
			PrintWriter out = new PrintWriter(new FileWriter(file, true));
		    out.println( line );
		    out.close();
        } catch(IOException e) {}	    
    }

    static File readFileFromCommandLine() {
        String fileName = null;
        File f = new File("fake/path");
        while (!f.exists()) {        
            System.out.print("Please enter the path to the card deck: ");
            try {
                fileName = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                f = new File(fileName);
                if(!f.exists()) {
                    System.out.println("It doesn't look like this file exists..");
                }
            } catch (IOException e) {
                System.out.println("Oops..somethign went wrong.");
                System.exit(1);
            }
        }
        return f;
    }

    static CardDeck fileToCardDeck(File f) throws FileNotFoundException, IOException {
        Scanner scanner = new Scanner(f);
        CardDeck initialDeck = new CardDeck(0, linesInAFile(f));
        while(scanner.hasNextInt()){
           initialDeck.push(new Card(scanner.nextInt()));
           // System.out.println("initial deck top: " + initialDeck.top().getValue());
        }
        scanner.close();
        return initialDeck;
    }
    
    /*
     * Deletes everything from the directory, if not empty
     */
    static void createNewDirectory(String dir_name) {
        File dir = new File(dir_name);
        dir.mkdir();
        for(File file : dir.listFiles()) file.delete();
    }

}
