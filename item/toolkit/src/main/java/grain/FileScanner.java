package grain;

import lombok.extern.slf4j.Slf4j;
import org.grain.tools.IO.Closeables;
import org.grain.tools.file.FileUtils;
import org.grain.tools.thread.CustomThreadPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

@Slf4j
public class FileScanner {
    public static List<File> getFile(String rootPath, String regex) {
        String s = "35";
        byte b = Byte.parseByte(s);
        return FileUtils.getFiles(new File(rootPath), Pattern.compile(regex));
    }

    public static void find(File file, String target) {
        int count = 0;
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                ++count;
                if (s.contains(target)) {
                    log.info("{} {} {}", file.getAbsolutePath(), count, s);
                }
            }
        } catch (Exception e) {
            log.warn("{}", e.getMessage());
        } finally {
            Closeables.close(bufferedReader);
            Closeables.close(inputStreamReader);
            Closeables.close(fileInputStream);
        }
    }

    public static void scanner(String file, String regex, String target) {
        List<File> files = getFile(file, regex);
        ThreadPoolExecutor threadPool = CustomThreadPool.createThreadPool("scanner-pool", 8, 8, false, 3000);
        files.forEach(a -> threadPool.submit(() -> find(a, target)));
    }
}
