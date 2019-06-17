package xyz.andrastoth.memehost.model;

import javax.persistence.*;

@Entity
@Table(name = "tbl_memes")
public class Meme {

    @Id
    String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_metadata_id", referencedColumnName = "id")
    FileMetadata fileMetadata;

    @OneToOne
    @JoinColumn(name = "thumbnail_file_metadata_id", referencedColumnName = "id")
    FileMetadata thumbnailFileMetadata;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public void setFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }

    public FileMetadata getThumbnailFileMetadata() {
        return thumbnailFileMetadata;
    }

    public void setThumbnailFileMetadata(FileMetadata thumbnailFileMetadata) {
        this.thumbnailFileMetadata = thumbnailFileMetadata;
    }

}
