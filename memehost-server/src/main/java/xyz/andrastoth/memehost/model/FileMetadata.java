package xyz.andrastoth.memehost.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_file_metadata")
public class FileMetadata {

    @Id
    private String storageFileName;
    private String storageThumbnailName;
    private String name;
    private String mimeType;
    private long size;

    public String getStorageFileName() {
        return storageFileName;
    }

    public void setStorageFileName(String storageFileName) {
        this.storageFileName = storageFileName;
    }

    public String getStorageThumbnailName() {
        return storageThumbnailName;
    }

    public void setStorageThumbnailName(String storageThumbnailName) {
        this.storageThumbnailName = storageThumbnailName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}