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
import xyz.andrastoth.memehost.model.Meme;
import xyz.andrastoth.memehost.service.MemeService;

import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/memes")
public class MemeController {

    @Autowired
    private MemeService memeService;

    @GetMapping(produces = "application/json")
    public List<Meme> getAllMemes() {
        return memeService.getAllMemes();
    }

    @DeleteMapping("/{id}")
    public void deleteMeme(@PathVariable String id) {
        memeService.deleteMemeById(id);
    }

    @GetMapping("/{id}")
    public Meme getMeme(@PathVariable("id") String id) {
        Meme meme = memeService.getMemeById(id);

        if (meme == null) {
            throw new ResourceNotFoundException();
        } else {
            return meme;
        }
    }

    @PostMapping("/upload")
    public Meme uploadMeme(@RequestParam("file") MultipartFile file) {
        return memeService.saveMeme(getInputStream(file), file.getOriginalFilename());
    }

    @RequestMapping(value = "/{id}/data", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadMeme(@PathVariable String id) throws IOException {

        Meme meme = memeService.getMemeById(id);
        File file = memeService.getFileById(id);

        if (meme == null || file == null || !file.exists()) {
            throw new ResourceNotFoundException();
        }

        return createFileResponse(meme.getFileMetadata(), file);
    }

    @RequestMapping(value = "/{id}/thumbnailData", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadMemeThumbnail(@PathVariable String id) throws IOException {

        Meme meme = memeService.getMemeById(id);

        if (meme == null || meme.getThumbnailFileMetadata() == null) {
            throw new ResourceNotFoundException();
        }

        File file = memeService.getFileById(meme.getThumbnailFileMetadata().getId());

        if (file == null || !file.exists()) {
            throw new ResourceNotFoundException();
        }

        return createFileResponse(meme.getFileMetadata(), file);
    }

    private ResponseEntity<InputStreamResource> createFileResponse(FileMetadata fileMetadata, File file) throws FileNotFoundException {
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