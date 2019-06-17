package xyz.andrastoth.memehost.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.andrastoth.memehost.Util;
import xyz.andrastoth.memehost.model.FileMetadata;
import xyz.andrastoth.memehost.model.Meme;
import xyz.andrastoth.memehost.repo.FileMetadataRepo;
import xyz.andrastoth.memehost.repo.FileStorageRepo;
import xyz.andrastoth.memehost.repo.MemeRepo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemeService {

    @Autowired
    private FileStorageRepo fileStorageRepo;

    @Autowired
    private FileMetadataRepo fileMetadataRepo;

    @Autowired
    private MemeRepo memeRepo;

    private static final Tika tika = new Tika();

    public Meme getMemeById(String id) {
        return memeRepo.findById(id).orElse(null);
    }

    public File getFileById(String id) {
        return fileStorageRepo.readFileFromStorage(id);
    }

    public File getMemeThumbnailFileById(String id) {
        Meme meme = memeRepo.findById(id).orElse(null);

        if(meme != null && meme.getThumbnailFileMetadata() != null) {
            FileMetadata thumbnailMetadata = meme.getThumbnailFileMetadata();

            return fileStorageRepo.readFileFromStorage(thumbnailMetadata.getId());
        } else {
            return null;
        }
    }

    public void deleteMemeById(String id) {
        Meme meme = memeRepo.findById(id).orElse(null);

        if(meme != null && meme.getThumbnailFileMetadata() != null) {
            FileMetadata thumbnailMetadata = meme.getThumbnailFileMetadata();
            fileMetadataRepo.deleteById(thumbnailMetadata.getId());
            fileStorageRepo.deleteFile(thumbnailMetadata.getId());
        }

        memeRepo.deleteById(id);
        fileStorageRepo.deleteFile(id);
    }

    public List<Meme> getAllMemes() {
        List<Meme> memes = new ArrayList<>();
        memeRepo.findAll().forEach(memes::add);
        return memes;
    }

    public Meme saveMeme(InputStream inputStream, String fileName) {

        FileMetadata fileMetadata = new FileMetadata();
        File tempFile = fileStorageRepo.createTempFile(inputStream);

        String mimeType = determineDataMimeType(tempFile);

        if (mimeType == null) {
            fileStorageRepo.deleteTempFile(tempFile);
            throw new UnsupportedMimeTypeException(mimeType);
        }
        fileMetadata.setMimeType(mimeType);

        String id = saveFileToStorage(tempFile, fileName);
        fileMetadata.setId(id);
        fileMetadata.setName(fileName);
        fileMetadata.setSize(tempFile.length());

        fileMetadata = fileMetadataRepo.save(fileMetadata);
        fileStorageRepo.deleteTempFile(tempFile);

        Meme meme = new Meme();
        meme.setId(id);
        meme.setFileMetadata(fileMetadata);
        return memeRepo.save(meme);
    }

    private String saveFileToStorage(File file, String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = FileUtils.openInputStream(file);
            String storageFileName = generateFileId(fileName);
            fileStorageRepo.writeStreamToStorage(storageFileName, inputStream);

            return storageFileName;
        } catch (Exception e) {

            throw new RuntimeException(e);
        } finally {
            Util.tryCloseStream((inputStream));
        }
    }

    private String generateFileId(String originalFileName) {
        String extension = FilenameUtils.getExtension(originalFileName);
        extension = extension == null ? "" : extension;
        String id = Util.generateUniqueId();
        return id + (extension.isEmpty() ? "" : ".") + extension;
    }


    private static String determineDataMimeType(File file) {
        InputStream inputStream = null;

        try {
            inputStream = FileUtils.openInputStream(file);

            return tika.detect(inputStream);
        } catch (IOException e) {

            return null;
        } finally {
            Util.tryCloseStream(inputStream);
        }
    }
}