package org.grain.web.server.constants;

/**
 * @author laowu
 * @version 6/3/2019 3:10 PM
 * http status
 */
public enum HttpStatus {
    /**
     * pass
     */
    OK(200),
    /**
     * not found
     */
    NOT_FOUND(404),
    /**
     * internal server error
     */
    INTERNAL_SERVER_ERROR(500),
    /**
     * bad request
     */
    BAD_REQUEST(400),
    /**
     * moved temporarily
     */
    MOVED_TEMPORARILY(302);
    private int code;

    HttpStatus(int code) {
        this.code = code;
    }
}
