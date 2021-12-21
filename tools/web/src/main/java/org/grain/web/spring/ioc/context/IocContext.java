package org.grain.web.spring.ioc.context;

import org.grain.web.spring.ioc.annotation.Component;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author laowu
 * @version 5/7/2019 2:51 PM
 */
public class IocContext {
    public static final Map<Class<?>, Object> APPLICATION_CONTEXT = new ConcurrentHashMap<>();
    private static final URL url;

    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        url = loader.getResource(IocContext.class.getPackage().getName());
        String packageName = "grain/spring/ioc";
        try {
            initBean(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initBean(String packageName) throws Exception {
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replaceAll("\\.", "/"));
        while (urls.hasMoreElements()) {
            addClassByAnnotation(urls.nextElement().getPath(), packageName);
        }
        //IOC实现， 自定注入
        IocUtil.inject();
    }

    /**
     * 获取指定包路径下实现 Component主键Bean的实例
     */
    private static void addClassByAnnotation(String filePath, String packageName) {
        try {
            File[] files = getClassFile(filePath);
            if (files != null) {
                for (File f : files) {
                    String fileName = f.getName();
                    if (f.isFile()) {
                        Class<?> clazz = Class.forName(packageName.replace("/", ".") + "." + fileName.substring(0, fileName.lastIndexOf(".")));
                        //判断该类是否实现了注解
                        if (clazz.isAnnotationPresent(Component.class)) {
                            APPLICATION_CONTEXT.put(clazz, clazz.getDeclaredConstructor().newInstance());
                        }
                    } else {
                        addClassByAnnotation(f.getPath(), packageName + "." + fileName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取该路径下所遇的class文件和目录
     */
    private static File[] getClassFile(String filePath) {
        return new File(filePath).listFiles(file -> file.isFile() && file.getName().endsWith(".class") || file.isDirectory());
    }
}

