package org.grain;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "service-provider",url = "http://localhost:8003")
public interface ServiceInterface {
    @RequestMapping(value = "get",method = RequestMethod.GET)
    Integer getId();
}
