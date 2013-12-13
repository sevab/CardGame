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

    static void appendLineToFile(File file, String line) {
        try {
			PrintWriter out = new PrintWriter(new FileWriter(file, true));
		    out.println( line );
		    out.close();
        } catch(IOException e) {
            System.out.println("Oops..somethign went wrong while writing a file.");
            System.exit(1);
        }
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

    // reads file to a CardDeck and validates file values all along
    static CardDeck fileToCardDeck(File f, int numberOfPlayers, int handSize) throws FileNotFoundException, IOException {
        Scanner scanner = new Scanner(f);
        int linesInAFile = linesInAFile(f);
        int deckSize = 2 * numberOfPlayers * handSize;
        if ( linesInAFile < deckSize)
            throw new RuntimeException("Insufficient number of cards in the initial deck. Please, import a larger deck");
        CardDeck initialDeck = new CardDeck(0, linesInAFile);

        int i = 0;
        int num;
        while (scanner.hasNext() && i < deckSize) {
            if (!scanner.hasNextInt())
                throw new NumberFormatException("It looks like the card deck you supplied contains non-numeric characters. \nPlease ensure that your card deck consists only of positive integers.");
            num = scanner.nextInt();
            if (num < 0)
                throw new NumberFormatException("It looks like the card deck you supplied contains negative integers. \nPlease ensure that your card deck consists only of positive integers.");
            if (num > numberOfPlayers)
                throw new NumberFormatException("It looks like the card deck you supplied contains an integer value exceeding the number of players you specified. \nIt is legal for the face value of a card to exceed number of players.");
            initialDeck.push(new Card(num));
            i++;
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
