package xyz.andrastoth.memehost.repo;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileStorageRepo {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    private static final String FILE_STORAGE_PATH;
    static {
        String userHomePath = System.getProperty("user.home") + "/memehost-files/";
        String fileStoragePath = System.getProperty("file-storage-path", userHomePath);
        fileStoragePath = !fileStoragePath.endsWith("/") ? fileStoragePath + "/" : fileStoragePath;
        FILE_STORAGE_PATH = fileStoragePath;
    }

    public void writeStreamToStorage(String fileName, InputStream inputStream){
        File targetFile = new File(FILE_STORAGE_PATH + fileName);
        try {
            FileUtils.copyInputStreamToFile(inputStream, targetFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File readFileFromStorage(String fileName) {
        File file = new File(FILE_STORAGE_PATH + fileName);

        if(!file.exists()) {
            throw new RuntimeException("File " + fileName + " not found.");
        }

        return file;
    }

    public void generateThumbnail(String fileName, InputStream inputStream){
        File targetFile = new File(FILE_STORAGE_PATH + fileName);

        try {
            Thumbnails.of(inputStream)
                    .size(600, 600)
                    .outputFormat("jpg")
                    .toFile(targetFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File createTempFile(InputStream inputStream) {
        String fileName = TEMP_DIR + (!TEMP_DIR.endsWith("/") ? "/" : "") + UUID.randomUUID().toString();
        File targetFile = new File(fileName);
        try {
            FileUtils.copyInputStreamToFile(inputStream, targetFile);
            return new File(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteTempFile(File file) {
        return file.delete();
    }

    public boolean deleteFile(String fileName) {
        File targetFile = new File(FILE_STORAGE_PATH + fileName);
        if(targetFile.exists()) {
            return targetFile.delete();
        } else {
            return false;
        }
    }

}