package org.grain.tools.ftp.pool.sftp;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author wulifu
 */
public class PooledSftpChannelBuilder {
    public static GenericObjectPool<ChannelSftp> build() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setLifo(true);
        genericObjectPoolConfig.setMaxTotal(15);
        genericObjectPoolConfig.setMaxIdle(15);
        genericObjectPoolConfig.setMaxWaitMillis(20000);
        genericObjectPoolConfig.setMinEvictableIdleTimeMillis(-1);
        genericObjectPoolConfig.setSoftMinEvictableIdleTimeMillis(-1);
        genericObjectPoolConfig.setMinIdle(0);
        genericObjectPoolConfig.setNumTestsPerEvictionRun(15);
        genericObjectPoolConfig.setTestOnBorrow(false);
        genericObjectPoolConfig.setTestOnReturn(false);
        genericObjectPoolConfig.setTestWhileIdle(true);
        genericObjectPoolConfig.setTestOnCreate(false);
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(10000);
        return new GenericObjectPool<>(new PooledSftpChannelFactory("gansu","1qaz@WSX","10.1.63.26","19222"), genericObjectPoolConfig);
    }
}
