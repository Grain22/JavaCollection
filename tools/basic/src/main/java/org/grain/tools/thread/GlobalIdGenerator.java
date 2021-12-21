package org.grain.tools.thread;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wulifu
 */
public class GlobalIdGenerator {

    /**
     * 机器id所占的位数
     */
    private static int workerIdBits = 5;

    /**
     * 数据标识id所占的位数
     */
    private static int dataCenterIdBits = 5;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private static long maxWorkerId = ~(-1L << workerIdBits);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private static long maxDataCenterId = ~(-1L << dataCenterIdBits);

    /**
     * 序列在id中占的位数
     */
    private static int sequenceBits = 12;

    /**
     * 数据标识id向左移3
     */
    private static int dataCenterIdShift = sequenceBits + workerIdBits;

    /**
     * 机器ID向左移12位
     */
    private static int workerIdShift = sequenceBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private static int timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private static long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 毫秒内序列(0~4095)
     */
    public static long sequence;

    /**
     * 上次生成ID的时间截
     */
    public static long lastTimestamp;

    /**
     * 获得下一个ID
     */
    public static long nextId(long workerId, long dataCenterId) {
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new RuntimeException("not correct params");
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new RuntimeException("not correct params");
        }
        long timestamp = System.currentTimeMillis();
        if (timestamp > lastTimestamp) {
            sequence = 0L;
        } else if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = getNextTimestamp(lastTimestamp);
            }
        } else {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence > 0) {
                timestamp = lastTimestamp;
            } else   //毫秒内序列溢出
            {
                timestamp = lastTimestamp + 1;
            }
        }
        lastTimestamp = timestamp;
        //移位并通过或运算拼到一起组成64位的ID
        return (timestamp << timestampLeftShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    /**
     * 解析雪花ID
     */
    public static String analyzeId(long id) {
        long timestamp = (id >> timestampLeftShift);
        long dataCenterId = (id ^ (timestamp << timestampLeftShift)) >> dataCenterIdShift;
        long workerId = (id ^ ((timestamp << timestampLeftShift) | (dataCenterId << dataCenterIdShift))) >> workerIdShift;
        long sequence = id & sequenceMask;
        return String.format("%s_%d_%d_%d", SIMPLE_DATE_FORMAT.format(new Date(timestamp)), dataCenterId, workerId, sequence);
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     */
    private static long getNextTimestamp(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
