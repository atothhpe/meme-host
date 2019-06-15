package xyz.andrastoth.memehost.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.andrastoth.memehost.model.FileMetadata;
import xyz.andrastoth.memehost.service.FileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/memes")
public class FileResource {

    @Autowired
    private FileService fileService;

    @GetMapping(produces = "application/json")
    public List<FileMetadata> getAllFileMetadata() {
        return fileService.getAllFilesMetadata();
    }

    @PostMapping("/upload")
    public FileMetadata uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.saveFile(getInputStream(file), file.getOriginalFilename());
    }

    @DeleteMapping("/{storageFileName}")
    public void deleteFile(@PathVariable String storageFileName) {
        fileService.deleteFileByStorageName(storageFileName);
    }

    @GetMapping("/{storageName}")
    public FileMetadata getFileMetadata(@PathVariable("storageName") String storageName) {
        FileMetadata metadata = fileService.getFileMetadataByStorageName(storageName);

        if(metadata == null) {
            throw new ResourceNotFoundException();
        } else {
            return metadata;
        }
    }

    @RequestMapping(value = "/{storageFileName}/data", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String storageFileName) throws IOException {
        FileMetadata fileMetadata = fileService.getFileMetadataByStorageName(storageFileName);
        File file = fileService.getFileByStorageFileName(storageFileName);

        if(fileMetadata == null || file == null || !file.exists()) {
            throw new ResourceNotFoundException();
        }

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.valueOf(fileMetadata.getMimeType()));

        respHeaders.setContentLength(file.length());
        respHeaders.setContentDispositionFormData("attachment", fileMetadata.getName());

        InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(inputStreamResource, respHeaders, HttpStatus.OK);
    }

    private static InputStream getInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}