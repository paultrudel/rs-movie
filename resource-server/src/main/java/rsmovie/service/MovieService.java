package rsmovie.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rsmovie.entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {

    Optional<Movie> findById(String id);
    Page<Movie> findAll(Pageable pageable);
    Page<Movie> findAllOrderByScore(Pageable pageable);
    Page<Movie> findByTopicId(Long id, Pageable pageable);
    List<Movie> findByTopicId(Long id);
    List<Movie> findByTopicIdOrderByScore(Long id);
    Movie save(Movie movie);
}
