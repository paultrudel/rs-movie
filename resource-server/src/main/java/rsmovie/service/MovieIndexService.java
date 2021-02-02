package rsmovie.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rsmovie.entity.Movie;

public interface MovieIndexService {

    public Page<Movie> searchMovies(String query, Pageable pageable);
}
