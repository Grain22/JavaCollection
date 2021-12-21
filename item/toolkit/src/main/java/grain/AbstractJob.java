package grain;

/**
 * @author wulifu
 * 任务统一接口
 */
public interface AbstractJob {
    /**
     * 主动方法,无返回值
     * @param object 可选参数
     */
    void run(Object[] object);
}
