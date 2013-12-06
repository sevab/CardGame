package cardgame;

import java.io.BufferedReader;
// import java.io.BufferedWriter;
import java.io.File;
// import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
// import java.io.FileWriter;
import java.io.IOException;
// import java.nio.channels.FileChannel;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

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
}
