package org.grain.designpattern.prototype;

import lombok.Data;
import org.grain.tools.data.ObjectUtils;

/**
 * @author laowu
 * @version 5/7/2019 12:08 PM
 */
@Data
public class Mail implements Cloneable {
    private String receiver;
    private String subject;
    private String appellation;
    private String context;
    private String tail;

    public Mail(AdvTemplate advTemplate) {
        this.context = advTemplate.getAdvContext();
        this.subject = advTemplate.getAdvSubject();
    }

    @Override
    public Mail clone() {
        Mail mail = null;
        try {
            mail = (Mail) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mail;
    }

    public Mail deepClone() {
        return ObjectUtils.deepClone(this);
    }
}

