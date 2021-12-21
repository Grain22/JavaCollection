package grain.utils;

import grain.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wulifu
 */
@Component
@Slf4j
public class FileUtils {

    public static final Map<Integer, List<String>> TEMPORARY_FILE_CACHE_POOL = new HashMap<Integer, List<String>>();
    public static final Map<Integer, Font> FONT_CACHE_POOL = new HashMap<>();
    public static final int CACHE_POOL_NUMBER = 100;
    public static final String BASE_RESOURCE = "files/";
    private static final Map<Integer, Map<Integer, String>> MATERIAL_CACHE_POOL;
    private static final Stack<Integer> MATERIAL_USE_STACK;
    private static final Font DEFAULT_FONT = new Font(Font.SERIF, Font.PLAIN, 30);
    public static String APP_ROOT_DIRECTORY;
    public static String APP_FONT_DIRECTORY;
    public static String APP_MATERIAL_DIRECTORY;
    public static String APP_TEMPORARY_DIRECTORY;
    public static String APP_CLASSIC_CASE_DIRECTORY;

    static {
        MATERIAL_CACHE_POOL = new ConcurrentHashMap<>(CACHE_POOL_NUMBER);
        MATERIAL_USE_STACK = new Stack<>();
        File jarfile = new ApplicationHome(FileUtils.class).getSource();
        if (jarfile.getName().endsWith(Strings._JAR)) {
            APP_ROOT_DIRECTORY = jarfile.getParentFile().toString() + "/";
        } else {
            APP_ROOT_DIRECTORY = jarfile.toString() + "/";
        }
        APP_ROOT_DIRECTORY = APP_ROOT_DIRECTORY + BASE_RESOURCE;
        APP_FONT_DIRECTORY = APP_ROOT_DIRECTORY + "font/";
        APP_MATERIAL_DIRECTORY = APP_ROOT_DIRECTORY + "material/";
        APP_TEMPORARY_DIRECTORY = APP_ROOT_DIRECTORY + "temporary/";
        APP_CLASSIC_CASE_DIRECTORY = APP_ROOT_DIRECTORY + "classics/";

        File file = new File(APP_FONT_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(APP_MATERIAL_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(APP_TEMPORARY_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(APP_CLASSIC_CASE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
