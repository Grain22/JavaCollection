package org.grain.rmi.constant;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author laowu
 * @version demo
 * @date 2019/6/21 18:31
 */
public interface Handler extends Remote {
    /**
     * rmi test
     * @param id id生成
     * @param centerId 节点序列号
     * @return id
     * @throws RemoteException
     */
    String getMsg(int id,int centerId) throws RemoteException;
}
