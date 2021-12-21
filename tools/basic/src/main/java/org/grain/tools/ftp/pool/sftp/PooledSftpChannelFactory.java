package org.grain.tools.ftp.pool.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author wulifu
 */
@Slf4j
public class PooledSftpChannelFactory implements PooledObjectFactory<ChannelSftp> {

    private Session session;
    private final String username;
    private final String password;
    private final String host;
    private final String port;

    public PooledSftpChannelFactory(String username, String password, String host, String port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        try {
            log.debug("init sftp client {} {} {} {}", host, port, username, password);
            session = new JSch().getSession(username, host, Integer.parseInt(port));
            session.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sshConfig);
            if (!session.isConnected()) {
                log.debug("session not connect , now to try");
                session.connect();
            }
            log.info("session check connection {}", session.isConnected());
        } catch (Exception e) {
            log.error("session connect failed case by {}", e.getMessage());
        }
    }

    @Override
    public PooledObject<ChannelSftp> makeObject() throws Exception {
        return new PooledSftpChannel((ChannelSftp) session.openChannel("sftp"));
    }

    @Override
    public void destroyObject(PooledObject<ChannelSftp> p) throws Exception {
        p.getObject().disconnect();
    }

    @Override
    public boolean validateObject(PooledObject<ChannelSftp> p) {
        try {
            List<String> fileList = new ArrayList<>();
            p.getObject().ls(".", entry -> {
                fileList.add(entry.getLongname());
                return ChannelSftp.LsEntrySelector.CONTINUE;
            });
            String collect = String.join(",", fileList);
            log.trace("sftp channel run ls result {}", collect);
            log.debug("sftp channel can use");
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<ChannelSftp> p) throws Exception {
        if (p.getObject().isConnected()) {
            return;
        }
        p.getObject().connect();
        if (p.getObject().isConnected()) {
            return;
        }
        throw new javax.naming.ServiceUnavailableException();
    }

    @Override
    public void passivateObject(PooledObject<ChannelSftp> p) throws Exception {

    }
}
