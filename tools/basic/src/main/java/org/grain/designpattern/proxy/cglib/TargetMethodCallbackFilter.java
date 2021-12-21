package org.grain.designpattern.proxy.cglib;

import net.sf.cglib.proxy.CallbackFilter;

import java.lang.reflect.Method;

/**
 * @author laowu
 * @version 5/6/2019 12:17 PM
 */
public class TargetMethodCallbackFilter implements CallbackFilter {

    @Override
    public int accept(Method method) {
        if ("getString".equals(method.getName())) {
            System.out.println("filter get string ");
            return 2;
        }
        return 0;
    }
}
