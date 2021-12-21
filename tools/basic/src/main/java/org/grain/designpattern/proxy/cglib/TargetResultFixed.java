package org.grain.designpattern.proxy.cglib;

import net.sf.cglib.proxy.FixedValue;

/**
 * @author laowu
 * @version 5/6/2019 12:23 PM
 */
public class TargetResultFixed implements FixedValue {

    /**
     * 该类实现FixedValue接口，同时锁定回调值为999
     * (整型，CallbackFilter中定义的使用FixedValue型回调的方法为getConcreteMethodFixedValue，该方法返回值为整型)。
     */
    @Override
    public Object loadObject() {
        System.out.println("锁定结果");
        return "12312313";
    }
}
