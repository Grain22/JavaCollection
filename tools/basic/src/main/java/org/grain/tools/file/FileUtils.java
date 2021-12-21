package org.grain.tools.file;

import lombok.extern.slf4j.Slf4j;
import org.grain.tools.CustomerLogger;
import org.grain.tools.IO.Closeables;
import org.grain.tools.data.DateUtils;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.io.File.separator;

/**
 * @author laowu
 */
@SuppressWarnings("unused")
@Slf4j
public class FileUtils {

    private static final String DEF_PATH = separator + "temporary";
    private static final String DEF_FILE_NAME = DateUtils.getTimeString() + ".log";
    private static final String DEF_FILE = DEF_PATH + separator + DEF_FILE_NAME;
    private static CustomerLogger logger = CustomerLogger.getLogger(FileUtils.class);
    private static boolean append = true;

    private static boolean isUnitTest() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (int i = stackTrace.length - 1; i >= 0; --i) {
                if (stackTrace[i].getClassName().startsWith("org.junit.")) {
                    return true;
                }
            }
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static File getRootJarFile(JarFile jarFile) {
        String name = jarFile.getName();
        int separator = name.indexOf("!/");
        if (separator > 0) {
            name = name.substring(0, separator);
        }

        return new File(name);
    }

    public static File findSource(URL location) throws IOException {
        URLConnection connection = location.openConnection();
        return connection instanceof JarURLConnection ? getRootJarFile(((JarURLConnection) connection).getJarFile()) : new File(location.getPath());
    }

    private static File findSource(Class<?> sourceClass) {
        try {
            ProtectionDomain domain = sourceClass != null ? sourceClass.getProtectionDomain() : null;
            CodeSource codeSource = domain != null ? domain.getCodeSource() : null;
            URL location = codeSource != null ? codeSource.getLocation() : null;
            File source = location != null ? findSource(location) : null;
            return source != null && source.exists() && !isUnitTest() ? source.getAbsoluteFile() : null;
        } catch (Exception var6) {
            return null;
        }
    }

    /**
     * write to file
     *
     * @param strings  string s
     * @param filePath path
     * @param fileName name
     */
    public static void writeToFile(List<String> strings, Charset charset, String filePath, String fileName, boolean autoFeed, boolean append) {
        writeToFile(strings, charset, filePath + separator + fileName, autoFeed, append);
    }

    public static void writeToFile(List<String> strings, Charset charset, String pathWithName, boolean autoFeed, boolean append) {
        try {
            File file;
            if (Objects.isNull(pathWithName)) {
                pathWithName = DEF_FILE;
            }
            file = new File(pathWithName);
            if (!file.getParentFile().exists()) {
                boolean mkdirs = file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, append);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, charset);
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);
            for (String tem : strings) {
                writer.write(tem);
                if (autoFeed) {
                    writer.write("\n");
                }
            }
            Closeables.close(writer);
            Closeables.close(outputStreamWriter);
            Closeables.close(fileOutputStream);
            logger.log("write to file : " + file.getAbsolutePath() + " success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read from file
     *
     * @param pathWithName path name
     * @return string list
     */
    public static List<String> readFromFile(String pathWithName) {
        return readFromFile(pathWithName, StandardCharsets.UTF_8);
    }

    public static List<String> readFromFile(String pathWithName, Charset encoding) {
        File file = new File(pathWithName);
        if (file.exists() && file.isFile()) {
            List<String> list = new ArrayList<>();
            try (InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), encoding)) {
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    list.add(lineTxt);
                }
                bufferedReader.close();
                read.close();
                return list;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        } else {
            throw new RuntimeException("找不到指定的文件");
        }
    }

    private static boolean deleteDir(File dir) {
        if (!dir.exists()) {
            return false;
        }
        if (dir.isDirectory()) {
            String[] childes = dir.list();
            if (childes != null) {
                for (String child : childes) {
                    if (!deleteDir(new File(dir, child))) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    public static void getAllFilePaths(File filePath, ArrayList<String> filePaths) {
        File[] files = filePath.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                filePaths.add(f.getPath());
                getAllFilePaths(f, filePaths);
            } else {
                filePaths.add(f.getPath());
            }
        }
    }

    public static File getFile(String s) {
        try {
            File file;
            file = new File(s);
            if (file.exists()) {
                return file;
            }
            if (!file.getParentFile().exists()) {
                boolean mkdirs = file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException("file util get file error");
        }
    }

    public static String getFileName(String url, String separator) {
        File file = new File(url);
        return file.getName();
    }

    public static String getFileName(Object url) {
        File file;
        if (url instanceof URL) {
            file = new File(((URL) url).getPath());
        } else {
            file = new File(String.valueOf(url));
        }
        return file.getName();
    }

    public static InputStream getResource(String path) {
        return FileUtils.class.getClassLoader().getResourceAsStream(path);
    }

    public static String getSuffix(String path) {
        String[] split = path.split("\\.");
        if (split.length > 0) {
            return split[split.length - 1];
        }
        return "";
    }

    public static List<File> getFiles(File root, Pattern compile) {
        List<File> files = new ArrayList<>();
        if (root.exists()) {
            if (root.isDirectory()) {
                log.debug("find path is dir now on {}", root.getAbsolutePath());
                for (File file : Objects.requireNonNull(root.listFiles())) {
                    files.addAll(getFiles(file, compile));
                }
            } else if (root.isFile()) {
                log.debug("find path is file now on {}", root.getAbsolutePath());
                String name = root.getName();
                Matcher matcher = compile.matcher(name);
                if (matcher.find()) {
                    log.info("add file {}", root.getAbsolutePath());
                    files.add(root);
                }
            }
        }
        return files;
    }
}
