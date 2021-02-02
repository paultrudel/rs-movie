package rsmovie.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rsmovie.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByMovieId(String id, Pageable pageable);
    List<Review> findByMovieId(String id);
    Page<Review> findByUserId(String id, Pageable pageable);
    List<Review> findByUserIdOrderByScoreDesc(String id);
    Optional<Review> findByMovieIdAndUserId(String movieId, String userId);
}
