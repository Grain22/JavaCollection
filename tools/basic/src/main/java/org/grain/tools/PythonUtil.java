package org.grain.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author laowu
 */
public class PythonUtil {
    static final String PYTHON = "python";

    /**
     * put commands to python and get result print to terminal
     *
     * @param path python file path
     * @param args commands
     * @return terminal result
     */
    public static List<String> connectToPy(String path, List<String> args) {
        args.add(0, path);
        args.add(0, PYTHON);
        List<String> result = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec(args.toArray(new String[0]));
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                line = new String(line.getBytes("GBK"));
                result.add(line);
            }
            in.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
