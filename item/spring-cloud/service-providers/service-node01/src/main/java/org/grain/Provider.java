package org.grain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableDiscoveryClient
@SpringBootApplication
public class Provider {
    public static void main(String[] args) {
        SpringApplication.run(Provider.class, args);
    }

    @Value("${server.port}")
    Integer port;

    @ResponseBody
    @GetMapping("/getId")
    public Integer getServicePort() {
        return port * 10;
    }
}
