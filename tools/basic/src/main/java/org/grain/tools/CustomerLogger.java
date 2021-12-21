package org.grain.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static java.io.File.separator;
import static java.util.regex.Pattern.compile;

/**
 * @author laowu 类似slf4j的日志,就是输出一下结果和位置
 * @date  11/13/2018 6:09 PM
 */
public class CustomerLogger {
    public static final String WORK_SPACE = "." + separator + "temporary" + separator + "write";
    String filepath;
    String filename;
    private Class<?> clazz;

    private CustomerLogger(Class<?> cla) {
        this.clazz = cla;
    }

    public static CustomerLogger getLogger(Class<?> cla) {
        return new CustomerLogger(cla);
    }

    @SuppressWarnings("unused")
    public String log() {
        return log("", (Object) null);
    }

    public final String log(Object str) {
        return log(str.toString(), null);
    }

    public String log(String str, Object... strList) {
        StringBuilder stringBuilder = new StringBuilder("");
        getDate(stringBuilder);
        getStackInfo(stringBuilder);
        stringBuilder.append(str);
        if (!Objects.isNull(strList)) {
            replaceArgs(stringBuilder, strList);
        }
        printTOfile(stringBuilder);
        System.out.println(stringBuilder);
        return stringBuilder.toString();
    }

    private void replaceArgs(StringBuilder str, Object[] strlist) {
        for (Object o : strlist) {
            compile("\\{}").matcher(str).replaceFirst(o.toString());
        }
    }

    private void getDate(StringBuilder str) {
        Date date = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");
        String time = format.format(date);
        str.append(time);
    }

    private void getStackInfo(StringBuilder str) {
        /*获取线程运行栈信息*/
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        for (StackTraceElement stackTraceElement : stack) {
            if (stackTraceElement.getClassName().equals(clazz.getName())) {
                str.append(stackTraceElement.getClassName()).append(":").append(stackTraceElement.getLineNumber()).append(" ");
            }
        }
    }

    private void printTOfile(StringBuilder str) {
        if (filepath == null || "".equals(filepath)) {
            filepath = WORK_SPACE;
        }

        File file = new File(filepath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

        if (filename == null || "".equals(filename)) {
            Date date = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String time = format.format(date);
            filename = time + ".log";
        }
        file = new File(filepath + File.separator + filename);
        if (!file.exists() || !file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(str.toString());
            fw.write("\r");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
