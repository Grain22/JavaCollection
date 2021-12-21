package grain.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wulifu
 */
@Component
@ConfigurationProperties(prefix = "self.properties")
@Data
public class GlobalParams {
    private String nodeId;
    private String hostAddress;
    private String hostPort;
}
