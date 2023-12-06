package mia.appspring.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private final String baseLocation = "all-chats-attachments";
    private final String baseLocationUpload = "temp-upload";
    private final String baseLocationDownload = "temp-download";
    private final String baseLocationDB = "DB";

    public String getBaseLocation() {
        return baseLocation;
    }

    public String getBaseLocationUpload() {
        return baseLocationUpload;
    }

    public String getBaseLocationDownload() {
        return baseLocationDownload;
    }

    public String getBaseLocationDB() {
        return baseLocationDB;
    }
}
