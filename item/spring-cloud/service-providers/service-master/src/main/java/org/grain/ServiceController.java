package org.grain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wulifu
 */
@Controller
public class ServiceController {
    @Value("${server.port}")
    Integer port;

    public ServiceController(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @ResponseBody
    @GetMapping("/getId")
    public Integer getServicePort(){
        return port;
    }

    protected final ServiceInterface serviceInterface;

    @ResponseBody
    @GetMapping("/getFrom")
    public Integer getFrom(){
        return serviceInterface.getId();
    }

}
