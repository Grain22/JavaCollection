package org.grain.tools.ftp.ftp;

import org.grain.tools.ftp.ShowProgressInfo;
import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpClientProvider;
import sun.net.ftp.FtpProtocolException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author grain
 */
public class FTPUtils {

    public static String target_host = "10.1.63.26";

    private static String port = "21";

    private String username = "gansu";

    private String password = "1qaz@WSX";

    FtpClient ftpClient = null;

    public static FtpClient getFTPClient(String HOST, String PORT, String NAME, String PWD) throws IOException, FtpProtocolException {
        FtpClient ftpClient = FtpClientProvider.provider().createFtpClient();
        SocketAddress socketAddress = new InetSocketAddress(HOST, Integer.parseInt(PORT));
        ftpClient.connect(socketAddress);
        if (ftpClient.isConnected()) {
            throw new RuntimeException("ftp path error");
        }
        ftpClient.login(NAME, PWD.toCharArray());
        if (ftpClient.isLoggedIn()) {
            ftpClient.close();
            throw new RuntimeException("account error");
        }
        ftpClient.setConnectTimeout(60000);
        ftpClient.setBinaryType();
        return ftpClient;
    }

    /**
     * 真正用于上传的方法
     *
     * @param local
     * @param target
     * @param path
     * @throws Exception
     */
    private void upload(String local, String target, String path)
            throws Exception {
        File localFile = new File(local);
        if (!localFile.exists()) {
            throw new Exception("file not exist");
        }
        if (localFile.isDirectory()) {
            File[] files = localFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (!files[i].exists()) {
                    continue;
                }
                if (files[i].isDirectory()) {
                    this.upload(files[i].getPath(), files[i].getName(), path + "/" + target);
                } else {
                    this.uploadFile(files[i].getPath(), files[i].getName());
                }
            }
        } else {
            uploadFile(localFile.getPath(), target);
        }
        ftpClient.changeDirectory(path);
    }

    public long uploadFile(String local, String target) throws Exception {
        long result = 0;
        TelnetOutputStream os = null;
        FileInputStream is = null;
        ShowProgressInfo info = new ShowProgressInfo();
        info.file = target;
        try {
            File localFile = new File(local);
            if (!localFile.exists()) {
                return -1;
            }
            result = localFile.length();
            info.total = result;
            info.start();
            OutputStream outputStream = ftpClient.putFileStream(target);
            os = new TelnetOutputStream(outputStream, true);
            is = new FileInputStream(localFile);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = is.read(bytes)) != -1) {
                os.write(bytes, 0, c);
                info.now += c;
            }
            info.join();
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
        return result;
    }

    /**
     * 从ftp下载文件到本地
     *
     * @param source 服务器上的文件名
     * @param target 本地生成的文件名
     * @return long for file length
     */
    public long downloadFile(String source, String target) {
        long result = 0;
        TelnetInputStream is = null;
        FileOutputStream os = null;
        ShowProgressInfo info = new ShowProgressInfo();
        info.file = target;
        try {
            InputStream fileStream = ftpClient.getFileStream(source);
            info.total = ftpClient.getSize(source);
            info.start();
            is = new TelnetInputStream(fileStream, true);
            File outfile = new File(target);
            os = new FileOutputStream(outfile);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = is.read(bytes)) != -1) {
                os.write(bytes, 0, c);
                info.now += c;
            }
            info.join();
        } catch (IOException | FtpProtocolException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
