package rsmovie.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rsmovie.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Optional<Review> findById(Long id);
    Page<Review> findAll(Pageable pageable);
    Page<Review> findByMovieId(String id, Pageable pageable);
    List<Review> findByMovieId(String id);
    Page<Review> findByUserId(String id, Pageable pageable);
    List<Review> findByUserIdOrderByScoreDesc(String id);
    Optional<Review> findByMovieIdAndUserId(String movieId, String userId);
    Review save(Review review);
    void delete(Review review);
}
