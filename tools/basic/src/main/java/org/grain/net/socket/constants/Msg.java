package org.grain.net.socket.constants;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wulifu
 */
public class Msg {
    private static int data_size = 1024;
    private static byte padding = 0x20;
    @Getter
    @Setter
    private byte[] msg = new byte[data_size];

    public void setData(int begin, int length, byte[] data) {
        for (int i = 0; i < length; i++) {
            msg[i + begin] = data[i];
        }
    }

    public byte[] getData(int begin, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = this.getMsg()[begin + i];
        }
        return result;
    }
}
