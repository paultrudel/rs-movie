package rsmovie.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rsmovie.dao.PredictionRepository;
import rsmovie.entity.Prediction;
import rsmovie.service.PredictionService;

import java.util.List;
import java.util.Optional;

@Service
public class PredictionServiceImpl implements PredictionService {

    private PredictionRepository predictionRepository;

    public PredictionServiceImpl(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    @Override
    public Optional<Prediction> findById(Long id) {
        return predictionRepository.findById(id);
    }

    @Override
    public Page<Prediction> findAll(Pageable pageable) {
        return predictionRepository.findAll(pageable);
    }

    @Override
    public Page<Prediction> findByMovieId(String id, Pageable pageable) {
        return predictionRepository.findByMovieId(id, pageable);
    }

    @Override
    public Page<Prediction> findByUserId(String id, Pageable pageable) {
        return predictionRepository.findByUserId(id, pageable);
    }

    @Override
    public Optional<Prediction> findByMovieIdAndUserId(String movieId, String userId) {
        return predictionRepository.findByMovieIdAndUserId(movieId, userId);
    }

    @Override
    public List<Prediction> findByUserIdOrderByScoreDesc(String id) {
        return predictionRepository.findByUserIdOrderByScoreDesc(id);
    }

    @Override
    public Prediction save(Prediction prediction) {
        return predictionRepository.save(prediction);
    }

    @Override
    public void delete(Prediction prediction) { predictionRepository.delete(prediction); }
}
