package server.dto;

import java.time.LocalDate;

public class RequestsInfo {

    public RequestsInfo(String id, String url, LocalDate timestamp) {
        this.id = id;
        this.url = url;
        this.timestamp = timestamp;
    }

    private String id;
    private String url;
    private LocalDate timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

}
