package grain.grain.service;


import org.grain.web.spring.framework.annotation.GService;

/**
 * @author laowu
 * @version 5/13/2019 4:20 PM
 */
@GService
public class DemoService {
    public String getInfo(String name) {
        return "123";
    }
}
