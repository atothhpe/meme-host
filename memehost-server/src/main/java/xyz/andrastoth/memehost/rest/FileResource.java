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

    @GetMapping("/metadata/{storageName}")
    public FileMetadata getFileMetadata(@PathVariable("storageName") String storageName) {
        FileMetadata metadata = fileService.getFileMetadaByStorageName(storageName);

        if(metadata == null) {
            throw new ResourceNotFoundException();
        } else {
            return metadata;
        }
    }

    @PostMapping("/upload")
    public FileMetadata uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.saveFile(getInputStream(file), file.getOriginalFilename());
    }

    @RequestMapping(value = "/download/{storageFileName}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadStuff(@PathVariable String storageFileName) throws IOException {
        FileMetadata fileMetadata = fileService.getFileMetadaByStorageName(storageFileName);
        File file = fileService.getFileByStorageFileName(storageFileName);

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.valueOf(fileMetadata.getMimeType()));

        respHeaders.setContentLength(fileMetadata.getSize());
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