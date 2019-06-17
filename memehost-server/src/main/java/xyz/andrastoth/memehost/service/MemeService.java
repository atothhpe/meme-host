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

    private static final Tika tika = new Tika();

    @Autowired
    private FileStorageRepo fileStorageRepo;

    @Autowired
    private FileMetadataRepo fileMetadataRepo;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private MemeRepo memeRepo;

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

        FileMetadata memeFileMetadata = new FileMetadata();
        Meme meme = new Meme();

        File tempFile = fileStorageRepo.createTempFile(inputStream, fileName);

        String id = saveFileToStorage(tempFile, fileName);
        memeFileMetadata.setId(id);
        meme.setId(id);
        memeFileMetadata.setName(fileName);
        memeFileMetadata.setSize(tempFile.length());

        String mimeType = determineDataMimeType(tempFile);
        if (mimeType == null) {
            fileStorageRepo.deleteTempFile(tempFile);
            throw new UnsupportedMimeTypeException(mimeType);
        }
        memeFileMetadata.setMimeType(mimeType);

        FileMetadata thumbnailFileMetadata = thumbnailService.createThumbnail(tempFile, mimeType);
        meme.setThumbnailFileMetadata(thumbnailFileMetadata);

        memeFileMetadata = fileMetadataRepo.save(memeFileMetadata);
        fileStorageRepo.deleteTempFile(tempFile);

        meme.setFileMetadata(memeFileMetadata);
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

            return tika.detect(file.getName());
        } catch (IOException e) {

            return null;
        } finally {
            Util.tryCloseStream(inputStream);
        }
    }
}