package org.grain.designpattern.proxy.cglib;


import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author laowu
 * @version 5/6/2019 12:11 PM
 */
public class TargetInterceptor implements MethodInterceptor {

    /**
     * 重写方法拦截在方法前和方法后加入业务
     * Object obj为目标对象
     * Method method为目标方法
     * Object[] params 为参数，
     * MethodProxy proxy CGlib方法代理对象
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy proxy) throws Throwable {
        System.out.println("before method run , do some thing change the value");
        Object result = proxy.invokeSuper(o, objects);
        System.out.println("after method run , do some thing change the value");
        return result;
    }
}
