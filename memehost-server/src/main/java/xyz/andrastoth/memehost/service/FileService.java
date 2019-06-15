package xyz.andrastoth.memehost.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.andrastoth.memehost.model.FileMetadata;
import xyz.andrastoth.memehost.repo.FileMetadataRepo;
import xyz.andrastoth.memehost.repo.FileStorageRepo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private FileStorageRepo fileStorageRepo;

    @Autowired
    private FileMetadataRepo fileMetadataRepo;

    private static final Tika tika = new Tika();

    public FileMetadata getFileMetadaByStorageName(String storageName) {
        return fileMetadataRepo.findById(storageName).orElse(null);
    }

    public List<FileMetadata> getAllFilesMetadata() {
        List<FileMetadata> fileMetadataList = new ArrayList<>();
        fileMetadataRepo.findAll().forEach(fileMetadataList::add);
        return fileMetadataList;
    }

    public FileMetadata saveFile(InputStream inputStream, String fileName) {
        FileMetadata fileMetadata = new FileMetadata();
        File tempFile = fileStorageRepo.createTempFile(inputStream);

        String mimeType = determineDataMimeType(tempFile);
        if (mimeType == null) {
            throw new UnsupportedMimeTypeException(mimeType);
        }
        fileMetadata.setMimeType(mimeType);

        if (mimeType.contains("image")) {
            String storageThumbnailName = generateThumbnail(tempFile);
            fileMetadata.setStorageThumbnailName(storageThumbnailName);
        }

        String storageFileName = saveFileToStorage(tempFile, fileName);

        fileMetadata.setName(fileName);
        fileMetadata.setSize(tempFile.length());
        fileMetadata.setStorageFileName(storageFileName);

        fileMetadataRepo.save(fileMetadata);
        fileStorageRepo.deleteTempFile(tempFile);

        return fileMetadata;
    }

    public File getFileByStorageFileName(String storageFileName) {
        return fileStorageRepo.readFileFromStorage(storageFileName);
    }


    private String generateThumbnail(File file) {
        InputStream inputStream = null;
        try {
            inputStream = FileUtils.openInputStream(file);

            String storageThumbnailName = generateUniqueIdForFile() + ".jpg";
            fileStorageRepo.generateThumbnail(storageThumbnailName, inputStream);
            inputStream.close();

            return storageThumbnailName;
        } catch (Exception e) {

            throw new RuntimeException(e);
        } finally {
            tryCloseInputStream(inputStream);
        }
    }

    private String saveFileToStorage(File file, String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = FileUtils.openInputStream(file);
            String storageFileName = generateStorageFileName(fileName);
            fileStorageRepo.writeStreamToStorage(storageFileName, inputStream);

            return storageFileName;
        } catch (Exception e) {

            throw new RuntimeException(e);
        } finally {
            tryCloseInputStream(inputStream);
        }
    }

    private String generateStorageFileName(String originalFileName) {
        String extension = FilenameUtils.getExtension(originalFileName);
        extension = extension == null ? "" : extension;

        String id = generateUniqueIdForFile();

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
            tryCloseInputStream(inputStream);
        }
    }

    private static String generateUniqueIdForFile() {
        return UUID.randomUUID().toString();
    }

    private static void tryCloseInputStream(InputStream is) {
        if (is == null) return;
        try {
            is.close();
        } catch (IOException ignored) {
        }
    }

}