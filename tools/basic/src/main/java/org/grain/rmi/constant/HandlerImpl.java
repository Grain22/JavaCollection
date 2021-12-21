package org.grain.rmi.constant;


import org.grain.tools.thread.GlobalIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author wulifu
 */
public class HandlerImpl extends UnicastRemoteObject implements Handler, Serializable {
    Logger log = LoggerFactory.getLogger(HandlerImpl.class);

    private static final long serialVersionUID = 1L;
    public HandlerImpl() throws RemoteException {
        super();
    }

    @Override
    public String getMsg(int id,int centerId) {
        long l = ID.nextId(id, 1);
        System.out.println(l);
        return String.valueOf(l);
    }
}

class ID extends GlobalIdGenerator{

}
