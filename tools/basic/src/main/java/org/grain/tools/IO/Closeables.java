package org.grain.tools.IO;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;

@Slf4j
public class Closeables {
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.warn("close catch {}", e.getMessage());
            }
        }
    }
}
