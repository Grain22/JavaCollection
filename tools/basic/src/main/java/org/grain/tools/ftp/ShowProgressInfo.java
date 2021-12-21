package org.grain.tools.ftp;

/**
 * @author wulifu
 */
public class ShowProgressInfo {
    public String file;
    public long total;
    public long now;

    Thread thread;

    public void start() {
        thread = new Thread(new ShowProgress(this));
        thread.start();
    }

    public void join() throws InterruptedException {
        thread.join();
    }
}
