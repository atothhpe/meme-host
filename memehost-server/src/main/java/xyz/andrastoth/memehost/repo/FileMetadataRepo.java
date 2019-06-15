package xyz.andrastoth.memehost.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import xyz.andrastoth.memehost.model.FileMetadata;

@Service
public interface FileMetadataRepo extends CrudRepository<FileMetadata, String> {}
