package org.grain.web.spring.ioc.service;

import org.grain.web.spring.ioc.annotation.Component;
import org.grain.web.spring.ioc.bean.Bean;

/**
 * @author laowu
 * @version 5/7/2019 2:48 PM
 */
@Component
public class DemoService {
    public Bean get() {
        Bean bean = new Bean();
        bean.setObject("reqwrewqrewq");
        return bean;
    }
}
