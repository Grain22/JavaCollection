package org.grain.tools.ftp;

/**
 * @author wulifu
 */
public class ShowProgress implements Runnable {
    ShowProgressInfo info;
    public ShowProgress(ShowProgressInfo info) {
        this.info = info;
    }
    @Override
    public void run() {
        System.out.print("download " + info.file + " ");
        while (true) {
            try {
                long now = info.now;
                long total = info.total;
                String s = now + "/" + total;
                System.out.print(s);
                Thread.sleep(500);
                for (int i = 0; i < s.length(); i++) {
                    System.out.print("\b");
                }
                if (info.total == info.now) {
                    System.out.print(" complete ");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }
}
