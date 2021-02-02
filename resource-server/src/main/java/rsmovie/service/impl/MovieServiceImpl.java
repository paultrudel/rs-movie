package rsmovie.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rsmovie.dao.MovieRepository;
import rsmovie.entity.Movie;
import rsmovie.service.MovieService;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Optional<Movie> findById(String id) {
        return movieRepository.findById(id);
    }

    @Override
    public Page<Movie> findAll(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    @Override
    public Page<Movie> findAllOrderByScore(Pageable pageable) {
        return movieRepository.findAllByOrderByScore(pageable);
    }

    @Override
    public Page<Movie> findByTopicId(Long id, Pageable pageable) {
        return movieRepository.findByTopicId(id, pageable);
    }

    @Override
    public List<Movie> findByTopicId(Long id) { return movieRepository.findByTopicId(id); }

    @Override
    public List<Movie> findByTopicIdOrderByScore(Long id) { return movieRepository.findByTopicIdOrderByScore(id); }

    @Override
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }
}
