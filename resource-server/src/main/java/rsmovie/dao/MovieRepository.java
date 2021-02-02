package rsmovie.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rsmovie.entity.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, String> {

    Page<Movie> findAllByOrderByScore(Pageable pageable);
    Page<Movie> findByTopicId(Long id, Pageable pageable);
    List<Movie> findByTopicId(Long id);
    List<Movie> findByTopicIdOrderByScore(Long id);
}
