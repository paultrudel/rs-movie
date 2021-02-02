package rsmovie.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rsmovie.entity.Prediction;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    Page<Prediction> findByMovieId(String id, Pageable pageable);
    Page<Prediction> findByUserId(String id, Pageable pageable);
    Optional<Prediction> findByMovieIdAndUserId(String movieId, String userId);
    List<Prediction> findByUserIdOrderByScoreDesc(String id);
}
