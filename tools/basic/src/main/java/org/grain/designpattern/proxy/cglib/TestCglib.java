package org.grain.designpattern.proxy.cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

/**
 * @author laowu
 * @version 5/6/2019 12:13 PM
 */
public class TestCglib {

    private static void callbackFilter() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Target.class);

        CallbackFilter callbackFilter = new TargetMethodCallbackFilter();

        /*
         *(1)callback1：方法拦截器
         *(2)NoOp.INSTANCE：这个NoOp表示no operator，即什么操作也不做，代理类直接调用被代理的方法不进行拦截。
         *(3)FixedValue：表示锁定方法返回值，无论被代理类的方法返回什么值，回调方法都返回固定值。
         */
        Callback noopCb = NoOp.INSTANCE;
        Callback cb1 = new TargetInterceptor();
        Callback fixedValue = new TargetResultFixed();
        Callback[] carry = new Callback[]{cb1, noopCb, fixedValue};
        /*enhancer.setCallback(new TargetInterceptor());*/
        enhancer.setCallbacks(carry);
        enhancer.setCallbackFilter(callbackFilter);
        Target target = (Target) enhancer.create();
        System.out.println(target);
        System.out.println(target.getString());
    }

    private static void surroundMethod() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Target.class);
        enhancer.setCallback(new TargetInterceptor());

        Target t = (Target) enhancer.create();

        System.out.println(t);
        System.out.println(t.getString());
        System.out.println(t.getInt());
    }
}
