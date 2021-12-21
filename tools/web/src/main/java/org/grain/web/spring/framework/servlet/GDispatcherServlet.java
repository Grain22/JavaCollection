package org.grain.web.spring.framework.servlet;

import org.grain.web.spring.framework.annotation.GController;
import org.grain.web.spring.framework.annotation.GInject;
import org.grain.web.spring.framework.annotation.GRequestMapping;
import org.grain.web.spring.framework.annotation.GService;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author laowu
 * @version 5/13/2019 3:31 PM
 */
public class GDispatcherServlet extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";
    private final Properties properties = new Properties();
    private final List<String> classes = new ArrayList<>();
    private final Map<String, Object> ioc = new HashMap<>();
    private final Map<String, Method> handlerMapping = new HashMap<>();

    public GDispatcherServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) {
        doLoadConfig(config.getInitParameter(LOCATION));
        doScanner(properties.getProperty("scanPackage"));
        doInstance();
        doAutowired();
        initHandlerMapping();
    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> aClass = entry.getValue().getClass();
            if (!aClass.isAnnotationPresent(GController.class)) {
                continue;
            }
            String baseUrl = "";
            if (aClass.isAnnotationPresent(GRequestMapping.class)) {
                GRequestMapping requestMapping = aClass.getAnnotation(GRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(GRequestMapping.class)) {
                    continue;
                }
                GRequestMapping requestMapping = method.getAnnotation(GRequestMapping.class);
                String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                handlerMapping.put(url, method);
                System.out.println("mapped " + url + " " + method);
            }

        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(GInject.class)) {
                    continue;
                }
                GInject gInject = field.getAnnotation(GInject.class);
                String beanName = gInject.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInstance() {
        if (classes.size() == 0) {
            return;
        }
        try {
            for (String className : classes) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(GController.class)) {
                    String beanName = clazz.getName();
                    ioc.put(beanName, clazz.getDeclaredConstructor().newInstance());
                } else if (clazz.isAnnotationPresent(GService.class)) {
                    GService service = clazz.getAnnotation(GService.class);
                    String beanName = service.value();
                    if (!"".equals(beanName.trim())) {
                        ioc.put(beanName, clazz.getDeclaredConstructor().newInstance());
                        continue;
                    }
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (clazz.isInterface()) {
                        for (Class<?> ifs : interfaces) {
                            ioc.put(ifs.getName(), clazz.getDeclaredConstructor().newInstance());
                        }
                    } else {
                        ioc.put(clazz.getName(), clazz.getDeclaredConstructor().newInstance());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String filePath) {
        URL url = this.getClass().getClassLoader().getResource("/" + filePath.replaceAll("\\.", "/"));
        assert url != null;
        File dir = new File(url.getFile());
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                doScanner(filePath + "." + file.getName());
            } else {
                classes.add(filePath + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    private void doLoadConfig(String resource) {
        InputStream inputStream = null;
        String[] paths = resource.split(";");
        for (String location : paths) {
            try {
                if (location.contains("classpath*")) {
                    String path = location.replaceFirst("classpath\\*:", "").replaceAll(";", "");
                    inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
                } else if (location.contains("classpath")) {
                    String path = location.replaceFirst("classpath:", "").replaceAll(";", "");
                    inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
                } else {
                    inputStream = this.getClass().getClassLoader().getResourceAsStream(location);
                }
                properties.load(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("..." + Arrays.toString(e.getStackTrace()).replaceAll("\\[|}]", "").replaceAll(",\\s", "\r\n"));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String endWithHTML = ".html";
        if (endWithHTML.endsWith(req.getRequestURI())) {
            try {
                String path = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("/")).getFile()).getParentFile().getParentFile().getPath();
                File file = new File(path + req.getRequestURI().replaceFirst(req.getContextPath(), ""));
                InputStream inputStream = new FileInputStream(file);
                int read;
                long l = System.currentTimeMillis();
                while ((read = inputStream.read()) != -1) {
                    resp.getOutputStream().write(read);
                }
                System.out.println((System.currentTimeMillis() - l));
                return;
            } catch (Exception e) {
                String errorMessage =
                        "HTTP/1.1 404 File Not Found\r\n" +
                                "Content-Type: text/html\r\n" +
                                "Content-Length: 23\r\n" +
                                "\r\n" +
                                "<h1>File Not Found</h1>";
                resp.getOutputStream().write(errorMessage.getBytes());
                return;
            }
        }
        if (this.handlerMapping.isEmpty()) {
            return;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replace("/api", "").replaceAll("/+", "/");
        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("4=4");
            return;
        }
        Map<String, String[]> parameterMap = req.getParameterMap();
        Method method = this.handlerMapping.get(url);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] paramValues = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class) {
                paramValues[i] = req;
            } else if (parameterType == HttpServletResponse.class) {
                paramValues[i] = resp;
            } else if (parameterType == String.class) {
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    String value = Arrays.toString(entry.getValue()).replaceAll("[\\[\\]]", "").replaceAll(",\\s", ",");
                    paramValues[i] = value;
                }
            }
        }
        try {
            String beanName = method.getDeclaringClass().getName();
            method.invoke(this.ioc.get(beanName), paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
