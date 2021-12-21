package org.grain.web.spring.ioc.context;

import org.grain.web.spring.ioc.annotation.Inject;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author laowu
 * @version 5/7/2019 2:57 PM
 */
public class IocUtil {
    public static void inject() {
        Map<Class<?>, Object> map = IocContext.APPLICATION_CONTEXT;
        try {
            for (Map.Entry<Class<?>, Object> entry : map.entrySet()) {
                Class<?> clazz = entry.getKey();
                Object obj = entry.getValue();
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Inject.class)) {
                        Class<?> fieldClazz = field.getType();
                        field.setAccessible(true);
                        Object fieldObj = map.get(fieldClazz);
                        field.set(obj, fieldObj);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
