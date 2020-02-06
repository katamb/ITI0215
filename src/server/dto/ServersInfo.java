package server.dto;

public class ServersInfo {

    public ServersInfo(String ip, String port, boolean alive) {
        this.ip = ip;
        this.port = port;
        this.alive = alive;
    }

    private String ip;
    private String port;
    private boolean alive;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getIpWithPort() {
        return ip + ":" + port;
    }
}
