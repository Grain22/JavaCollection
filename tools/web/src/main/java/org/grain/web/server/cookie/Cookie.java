package org.grain.web.server.cookie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author laowu
 * @version 6/3/2019 2:14 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cookie {
    private String key;
    private String value;
}
