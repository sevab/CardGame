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
// import java.nio.channels.FileChannel;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
import java.io.PrintWriter;

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

    static void appendLineToFile(File file, String line) throws IOException{
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
}
