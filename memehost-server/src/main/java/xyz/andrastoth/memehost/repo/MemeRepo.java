package xyz.andrastoth.memehost.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import xyz.andrastoth.memehost.model.Meme;

@Service
public interface MemeRepo extends CrudRepository<Meme, String> {}
