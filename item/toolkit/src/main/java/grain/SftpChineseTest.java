package grain;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import org.grain.tools.ftp.sftp.SftpSession;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author wulifu
 * sftp 中文名测试
 */
public class SftpChineseTest {
    public static void main(String[] args) throws Exception {
        SftpSession gansu = new SftpSession("10.1.63.26", "19222", "gansu", "1qaz@WSX");
        Session session = gansu.getSession();
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        File file = new File("C:\\Users\\wulifu\\Desktop\\3.docx");
        if (file.exists()) {
            sftp.put(new FileInputStream(file),"./3.docx");
        }
        System.out.println("");
    }
}
