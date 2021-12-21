package org.grain.tools.ftp.sftp;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author wulifu
 */
@Slf4j
public class SftpSession {
    Session session = null;
    String host;
    String port;
    String username;
    String password;
    static long out_time = 3000L;


    public SftpSession(String host, String port, String username, String password) {
        log.debug("init sftp client {} {} {} {}", host, port, username, password);
        this.host = host;
        this.password = password;
        this.port = port;
        this.username = username;
        getSession();
    }

    public static synchronized void getLock(ChannelSftp sftp, String s) {
        try {
            String prefixWithRegex = s.substring(0, s.lastIndexOf("_") + 1) + "*";
            while (true) {
                if (sftp.ls(prefixWithRegex).isEmpty()) {
                    sftp.put(new ByteArrayInputStream("sftp lock".getBytes()), s);
                    break;
                } else {
                    if (sftp.ls(prefixWithRegex).size() > 1) {
                        log.error("加锁操作出现错误,删除所有锁,退出");
                        Enumeration elements = sftp.ls(prefixWithRegex).elements();
                        while (elements.hasMoreElements()) {
                            sftp.rm(((ChannelSftp.LsEntry) elements.nextElement()).getFilename());
                        }
                        break;
                    } else {
                        ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) sftp.ls(prefixWithRegex).get(0);
                        long l = Long.parseLong(lsEntry.getFilename().substring(lsEntry.getFilename().lastIndexOf("_") + 1));
                        if (System.currentTimeMillis() - l > out_time) {
                            log.error("加锁时间超过五分钟,检查相关流程或者sftp网络环境");
                            sftp.rm(lsEntry.getFilename());
                            break;
                        } else {
                            Thread.sleep(100);
                        }
                    }
                }
            }
        } catch (SftpException | InterruptedException e) {
            log.error("sftp 上传锁失败", e);
        }
    }

    public static synchronized void releaseLock(ChannelSftp sftp, String s) {
        try {
            sftp.rm(s);
        } catch (SftpException e) {
            log.error("操作锁失败");
        }
    }

    public Session getSession() {
        try {
            if (session == null) {
                log.debug("session is null begin to init");
                log.debug("init sftp client {} {} {} {}", host, port, username, password);
                session = new JSch().getSession(username, host, Integer.parseInt(port));
                session.setPassword(password);
                Properties sshConfig = new Properties();
                sshConfig.put("StrictHostKeyChecking", "no");
                session.setConfig(sshConfig);
            }
            if (!session.isConnected()) {
                log.debug("session not connect , now to try");
                session.connect();
            }
            log.info("session check connection {}", session.isConnected());
        } catch (Exception e) {
            log.error("session connect failed case by {}", e.getMessage());
        }
        return session;
    }

    public static final String LINUX_SEPARATOR = "/";
    public static final String LINUX_NOW = ".";
    public static final String LINUX_HOME = "~";

    public static boolean moveToDir(ChannelSftp sftp, String dir, boolean antCreate) {
        try {
            if (dir.startsWith(LINUX_SEPARATOR)) {
                sftp.cd(LINUX_SEPARATOR);
            }
            if (dir.startsWith(LINUX_NOW) || dir.startsWith(LINUX_HOME)) {
                sftp.cd(sftp.getHome());
            }
            String[] split = dir.split(LINUX_SEPARATOR);
            for (String s : split) {
                if (s.isEmpty() || LINUX_NOW.equals(s) || LINUX_HOME.equals(s)) {
                    continue;
                }
                try {
                    log.debug("sftp channel path now : {} try to : {}", sftp.pwd(), s);
                    sftp.cd(s);
                    log.debug("now on {}", sftp.pwd());
                } catch (Exception e) {
                    log.debug("path not exist {} : {}", sftp.pwd(), s);
                    if (antCreate) {
                        log.debug("path not exist , now to create");
                        sftp.mkdir(s);
                        sftp.cd(s);
                        log.debug("now on {}", sftp.pwd());
                    } else {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("path not exist and antCreateConfig is {}", antCreate);
            return false;
        }
    }

    public void lockFileExample() throws JSchException, SftpException, IOException {
        String remotePath = "/";
        String recClientId = "000000";
        String fileLock = "000000" + "_lock_" + System.currentTimeMillis();
        int count = 0;
        ChannelSftp sftp = (ChannelSftp) getSession().openChannel("sftp");
        String sftpGlobalCount = "sftp_global_count_do_not_remove";
        sftp.cd("..");
        SftpSession.getLock(sftp, fileLock);
        try {
            InputStream inputStream = sftp.get(sftpGlobalCount);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String s = bufferedReader.readLine();
            int before = Integer.parseInt(s);
            if (before == out_time) {
                count = 0;
            } else {
                count = before + 1;
            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            log.debug("初次创建计数器");
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(("" + count).getBytes());
        sftp.put(byteArrayInputStream, sftpGlobalCount);
        byteArrayInputStream.close();
        SftpSession.releaseLock(sftp, fileLock);
        SftpSession.moveToDir(sftp, remotePath, true);
    }
}
