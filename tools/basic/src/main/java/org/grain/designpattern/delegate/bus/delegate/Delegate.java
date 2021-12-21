package org.grain.designpattern.delegate.bus.delegate;

import org.grain.designpattern.delegate.bus.service.BusinessService;
import org.grain.designpattern.delegate.query.Query;

/**
 * @author laowu
 * @version 5/6/2019 2:08 PM
 */
public class Delegate {

    private Query query = new Query();
    private BusinessService businessService;
    private String serType;

    public void setServiceType(String serType) {
        this.serType = serType;
    }

    public void doTask() {
        businessService = query.getService(serType);
        businessService.doProcessing();
    }
}
