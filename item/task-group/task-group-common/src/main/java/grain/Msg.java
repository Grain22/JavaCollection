package grain;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wulifu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Msg {
    public static final int code_success = 200;
    public static final int code_unhandled_error = 500;
    public static final int code_file_transfer_error = 501;
    public static final int code_task_info_error = 502;
    private int code;
    private String message;
    private Object data;

    public static Msg success() {
        return new Msg(code_success, "success", null);
    }

    public static Msg success(Object data) {
        return new Msg(code_success, "success", data);
    }

    public static Msg error(int code, String message) {
        return new Msg(code, message, null);
    }

    public static Msg parse(String s) {
        return JSON.parseObject(s, Msg.class);
    }
}
