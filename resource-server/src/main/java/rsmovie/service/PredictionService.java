package rsmovie.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rsmovie.entity.Prediction;

import java.util.List;
import java.util.Optional;

public interface PredictionService {

    Optional<Prediction> findById(Long id);
    Page<Prediction> findAll(Pageable pageable);
    Page<Prediction> findByMovieId(String id, Pageable pageable);
    Page<Prediction> findByUserId(String id, Pageable pageable);
    Optional<Prediction> findByMovieIdAndUserId(String movieId, String userId);
    List<Prediction> findByUserIdOrderByScoreDesc(String id);
    Prediction save(Prediction prediction);
    void delete(Prediction prediction);
}
