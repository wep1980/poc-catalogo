package br.com.wepdev.poc_catalogo.exception;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class StandardError implements Serializable {

    private  String message;
    private Integer status;
    private OffsetDateTime timestamp;
    private String path;
    private String error;

    public StandardError() {}


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
