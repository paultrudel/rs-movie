package rsmovie.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rsmovie.service.auth.AuthError;
import rsmovie.service.auth.AuthResponse;
import rsmovie.entity.*;
import rsmovie.service.*;
import rsmovie.utility.Recommendations;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin("http://localhost:4200")
public class RsMovieController  {

    private CommunityService communityService;
    private MovieService movieService;
    private ReviewService reviewService;
    private TermService termService;
    private TopicService topicService;
    private UserService userService;
    private PredictionService predictionService;
    private MovieIndexService movieIndexService;
    private RecommendationsService recommendationsService;
    private HttpService httpService;

    public RsMovieController(
            CommunityService communityService,
            MovieService movieService,
            ReviewService reviewService,
            TermService termService,
            TopicService topicService,
            UserService userService,
            PredictionService predictionService,
            MovieIndexService movieIndexService,
            RecommendationsService recommendationsService,
            HttpService httpService
    ) {
        this.communityService = communityService;
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.termService = termService;
        this.topicService = topicService;
        this.userService = userService;
        this.predictionService = predictionService;
        this.movieIndexService = movieIndexService;
        this.recommendationsService = recommendationsService;
        this.httpService = httpService;
    }

    @PostMapping("/users")
    public AuthResponse addUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        User user = new User();
        user.setUsername(username);
        user.setAvgScore(0.0);

        int registerResponse = httpService.registerUser(user, password);

        if(registerResponse < 300) {
            userService.save(user);
            AuthResponse response = httpService.authenticateUser(username, password);
            return response;
        }

        AuthError error = new AuthError();
        error.setError("Error");
        error.setErrorDescription("An unknown error occurred");
        return error;
    }

    @GetMapping("/movies/{id}")
    public Movie findMovie(@PathVariable String id) {
        Movie movie = movieService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return movie;
    }

    @GetMapping("/movies/search")
    public Page<Movie> searchMovies(
            @RequestParam("query") String query,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return movieIndexService.searchMovies(query, pageable);
    }

    @PostMapping("/reviews")
    public Page<Review> addReview(
            @RequestParam("username") String username,
            @RequestParam("movieId") String movieId,
            @RequestParam("score") Long score,
            @RequestParam("review") String review
    ) {
        Optional<User> userResult = userService.findByUsername(username);

        if(userResult.isPresent()) {
            User user = userResult.get();
            Optional<Movie> movieResult = movieService.findById(movieId);

            if(movieResult.isPresent()) {
                Movie movie = movieResult.get();
                Optional<Prediction> predictionResult = predictionService.findByMovieIdAndUserId(
                        movie.getId(), user.getId()
                );

                if(predictionResult.isPresent()) {
                    Prediction prediction = predictionResult.get();
                    predictionService.delete(prediction);
                } else {
                    Optional<Review> reviewResult = reviewService.findByMovieIdAndUserId(
                            movie.getId(), user.getId()
                    );

                    if(reviewResult.isPresent()) {
                        Review prevReview = reviewResult.get();
                        reviewService.delete(prevReview);
                    }
                }

                Review newReview = new Review();
                newReview.setScore(score);
                newReview.setContent(review);
                movie.addReview(newReview);
                user.addReview(newReview);

                List<Review> movieReviews = reviewService.findByMovieId(movie.getId());

                double newScore = movie.getScore() * (movieReviews.size() - 1);
                newScore += score;
                newScore = newScore / movieReviews.size();

                movie.setScore(newScore);

                movieService.save(movie);
                userService.save(user);
            } else {
                findReviewsByMovie(movieId, 1, 5);
            }
        } else {
            findReviewsByMovie(movieId, 1, 5);
        }

        return findReviewsByMovie(movieId, 1, 5);
    }

    @GetMapping("/reviews/{id}")
    public Review findReview(@PathVariable Long id) {
        Review review = reviewService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return review;
    }

    @GetMapping("/reviews/movie/{movieId}")
    public Page<Review> findReviewsByMovie(
            @PathVariable String movieId,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewService.findByMovieId(movieId, pageable);
    }

    @GetMapping("/users/{id}")
    public User findUser(@PathVariable String id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return user;
    }

    @GetMapping("/users/find/{username}")
    public User findUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return user;
    }

    @GetMapping("/users/recommendations/{username}")
    public Recommendations fetchUserRecommendations(@PathVariable String username) {
        Optional<User> result = userService.findByUsername(username);
        Recommendations recommendations = new Recommendations();
        if(result.isPresent()) {
            recommendations = recommendationsService.fetchRecommendations(result.get());
            return recommendations;
        }

        return recommendations;
    }

}
