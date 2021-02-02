package rsmovie.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rsmovie.entity.*;
import rsmovie.service.MovieService;
import rsmovie.service.PredictionService;
import rsmovie.service.RecommendationsService;
import rsmovie.service.ReviewService;
import rsmovie.utility.Recommendations;

import java.util.*;

@Service
public class RecommendationsServiceImpl  implements RecommendationsService {

    MovieService movieService;
    PredictionService predictionService;
    ReviewService reviewService;

    @Autowired
    public RecommendationsServiceImpl(
            MovieService movieService,
            PredictionService predictionService,
            ReviewService reviewService
    ) {
        this.movieService = movieService;
        this.predictionService = predictionService;
        this.reviewService = reviewService;
    }

    @Override
    public Recommendations fetchRecommendations(User user) {
        Community community = user.getCommunity();
        Set<Topic> topics = community.getTopics();

        List<Movie> communityMovies = new ArrayList<>();
        for(Topic topic: topics) {
            List<Movie> moviesByTopic = movieService.findByTopicId(topic.getId());
            communityMovies.addAll(moviesByTopic);
        }

        communityMovies.sort(Comparator.comparingDouble(Movie::getScore).reversed());

        Recommendations recommendations = new Recommendations();
        recommendations.setCommunity(communityMovies.subList(0, 5));

        List<Movie> personalMovies = new ArrayList<>();
        List<Prediction> predictions = predictionService.findByUserIdOrderByScoreDesc(user.getId());

        int predictionCount = 0;
        for(Prediction prediction: predictions) {
            if(predictionCount < 5) {
                 personalMovies.add(prediction.getMovie());
            }
            predictionCount++;
        }

        recommendations.setPersonal(personalMovies);

        List<Movie> reviewedMovies = new ArrayList<>();
        List<Review> reviews = reviewService.findByUserIdOrderByScoreDesc(user.getId());

        int reviewCount = 0;
        for(Review review: reviews) {
            if(reviewCount < 5) {
                reviewedMovies.add(review.getMovie());
            }
            reviewCount++;
        }

        recommendations.setReviewed(reviewedMovies);

        return recommendations;
    }
}
