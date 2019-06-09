package xyz.andrastoth.memehost.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.andrastoth.memehost.model.FileMetadata;
import xyz.andrastoth.memehost.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/memes")
public class FileResource {

    @Autowired
    private FileService fileService;

    @GetMapping(produces = "application/json")
    public List<FileMetadata> getAllImagesData() {
        return fileService.getAllFilesMetadata();
    }

    @PostMapping("/upload")
    public FileMetadata uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.saveFile(getInputStream(file), file.getOriginalFilename());
    }

    private static InputStream getInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}