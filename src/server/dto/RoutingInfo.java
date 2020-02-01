package server.dto;

public class RoutingInfo {

    public RoutingInfo(String id, String downloadIp, String fileIp) {
        this.id = id;
        this.downloadIp = downloadIp;
        this.fileIp = fileIp;
    }

    private String id;
    private String downloadIp;
    private String fileIp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownloadIp() {
        return downloadIp;
    }

    public void setDownloadIp(String downloadIp) {
        this.downloadIp = downloadIp;
    }

    public String getFileIp() {
        return fileIp;
    }

    public void setFileIp(String fileIp) {
        this.fileIp = fileIp;
    }

}
