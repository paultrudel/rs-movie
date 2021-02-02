package rsmovie.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rsmovie.dao.ReviewRepository;
import rsmovie.entity.Review;
import rsmovie.service.ReviewService;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    @Override
    public Page<Review> findByMovieId(String id, Pageable pageable) {
        return reviewRepository.findByMovieId(id, pageable);
    }

    @Override
    public List<Review> findByMovieId(String id) {
        return reviewRepository.findByMovieId(id);
    }

    @Override
    public Page<Review> findByUserId(String id, Pageable pageable) {
        return reviewRepository.findByMovieId(id, pageable);
    }

    @Override
    public List<Review> findByUserIdOrderByScoreDesc(String id) {
        return reviewRepository.findByUserIdOrderByScoreDesc(id);
    }

    @Override
    public Optional<Review> findByMovieIdAndUserId(String movieId, String userId) {
        return reviewRepository.findByMovieIdAndUserId(movieId, userId);
    }

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public void delete(Review review) { reviewRepository.delete(review);}
}
