package app;

public class PendingFileDownloadInfo {
    private final int receiverPort;
    private String fileName;

    public PendingFileDownloadInfo(int receiverPort) {
        this.receiverPort = receiverPort;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
