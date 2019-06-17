package xyz.andrastoth.memehost.service;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.andrastoth.memehost.model.FileMetadata;
import xyz.andrastoth.memehost.repo.FileMetadataRepo;
import xyz.andrastoth.memehost.repo.FileStorageRepo;

import java.io.*;

import static xyz.andrastoth.memehost.Util.generateUniqueId;
import static xyz.andrastoth.memehost.Util.tryCloseStream;

@Service
public class ThumbnailService {

    @Autowired
    private FileStorageRepo fileStorageRepo;

    @Autowired
    private FileMetadataRepo fileMetadataRepo;

    public FileMetadata createThumbnail(File file, String mimeType) {
        if(mimeType != null && mimeType.contains("image")) {
            return createImageThumbnail(file);
        } else {
            return null;
        }
    }

    /**
     * Hope we have enough memory #performance
     */
    private FileMetadata createImageThumbnail(File file) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;

        FileMetadata fileMetadata = new FileMetadata();

        String thumbnailId = generateUniqueId() + ".jpg";
        fileMetadata.setId(thumbnailId);
        fileMetadata.setName(thumbnailId);
        fileMetadata.setMimeType("image/jpg");

        try {
            inputStream = FileUtils.openInputStream(file);

            outputStream = new ByteArrayOutputStream();
            Thumbnails.of(inputStream)
                    .size(600, 600)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            fileMetadata.setSize((long) outputStream.size());

            fileStorageRepo.writeStreamToStorage(thumbnailId, new ByteArrayInputStream(outputStream.toByteArray()));
            fileMetadata = fileMetadataRepo.save(fileMetadata);

        } catch (Exception e) {

            try {
                fileStorageRepo.deleteFile(thumbnailId);
                fileMetadataRepo.deleteById(thumbnailId);
            } catch (Exception ignored){}

            throw new RuntimeException(e);

        } finally {
            tryCloseStream(outputStream);
            tryCloseStream(inputStream);
        }

        return fileMetadata;
    }

}