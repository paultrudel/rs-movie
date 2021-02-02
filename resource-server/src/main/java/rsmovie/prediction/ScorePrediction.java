package rsmovie.prediction;

import com.opencsv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsmovie.dao.RsDAO;
import rsmovie.entity.Movie;
import rsmovie.entity.Prediction;
import rsmovie.entity.Review;
import rsmovie.entity.User;
import rsmovie.utility.DataHandler;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ScorePrediction {

    private static final Logger logger = LoggerFactory.getLogger(ScorePrediction.class);

    public static final String SCORES_DIRECTORY = DataHandler.DATA_DIRECTORY + "/scores";

    protected RsDAO rsDAO;
    protected List<User> users;
    protected List<Movie> movies;
    protected List<Prediction> scoresToPredict;
    protected Map<String, Double> movieBiases;
    protected Map<String, Double> userBiases;
    protected Map<String, Double> userAverageScores;
    protected double avgMovieScore;
    protected double[][] actualMovieScores;
    protected double[][] predictedMovieScores;

    public ScorePrediction() {
        rsDAO = RsDAO.getInstance();
        constructScoreMatrices();
        computeBiases();
    }

    private void constructScoreMatrices() {
        logger.info("========= Constructing movie score matrix =========");

        logger.info("Fetching movies from database");
        movies = rsDAO.findAll(Movie.class);
        logger.info("{} movies found", movies.size());

        logger.info("Fetching users from database");
        users = rsDAO.findAll(User.class);
        logger.info("{} users found", users.size());

        actualMovieScores = new double[users.size()][movies.size()];
        predictedMovieScores = new double[users.size()][movies.size()];

        double totalScore = 0;
        double numScores = 0;

        List<Review> reviews = rsDAO.findAll(Review.class);

        for(Review review: reviews) {
            User user = review.getUser();
            int userIndex = users.indexOf(user);

            Movie movie = review.getMovie();
            int movieIndex = movies.indexOf(movie);

            long score = review.getScore();

            logger.info("User index {} movie index {}", userIndex, movieIndex);

            logger.info("Adding review by user {} for movie {} with a score of {}",
                    user.getId(), movie.getId(), score);

            actualMovieScores[userIndex][movieIndex] = score;
            predictedMovieScores[userIndex][movieIndex] = score;
            totalScore += score;
            numScores++;
        }

        scoresToPredict = new ArrayList<>();

        for(int u = 0; u < users.size(); u++) {
            User user = users.get(u);
            for(int m = 0; m < movies.size(); m++) {
                Movie movie = movies.get(m);

                double score = actualMovieScores[u][m];

                if(score == 0) {
                    logger.info("User {} has not reviewed {}. Must predict score.",
                            user.getId(), movie.getId());
                    Prediction prediction = new Prediction();
                    prediction.setMovie(movie);
                    prediction.setUser(user);
                    scoresToPredict.add(prediction);
                }
            }
        }

        avgMovieScore = totalScore / numScores;

        logger.info("========= Finished constructing score matrix =========");
    }

    private void computeBiases() {
        computeMovieBiases();
        computerUserBiases();
    }

    private void computeMovieBiases() {
        logger.info("========= Computing movie rating biases ==========");
        movieBiases = new HashMap<>();

        for(Movie movie: movies) {
            double movieBias = movie.getScore() - avgMovieScore;
            movieBiases.put(movie.getId(), movieBias);
            logger.info("Movie {} has a bias of {}", movie.getId(), movieBias);
        }

        logger.info("========= Finished computing movie biases =========");
    }

    private void computerUserBiases() {
        logger.info("========= Computing user average scores and biases =========");
        userBiases = new HashMap<>();
        userAverageScores = new HashMap<>();

        for(User user: users) {
            double userAverageScore = user.getAvgScore();
            double userBias = userAverageScore - avgMovieScore;
            userBiases.put(user.getId(), userBias);
            userAverageScores.put(user.getId(), userAverageScore);
            logger.info("User {} has an average score of {} and a bias of {}",
                    user.getId(), userAverageScore, userBias);
        }

        logger.info("========= Finished computing user biases =========");
    }

    protected void exportScores(String fileName) {
        logger.info("========== Exporting predicted scores to {}", fileName + ".csv");
        try {
            Writer writer = new FileWriter(SCORES_DIRECTORY + "/" + fileName + ".csv");
            CSVWriter csvWriter = new CSVWriter(writer);

            String[] mIds = new String[movies.size() + 1];
            mIds[0] = "";
            for(int m = 0; m < movies.size(); m++) {
                mIds[m + 1] = movies.get(m).getId();
            }
            csvWriter.writeNext(mIds);

            for(int u = 0; u < users.size(); u++) {
                String[] scores = new String[movies.size() + 1];
                scores[0] = users.get(u).getId();
                for(int m = 0; m < movies.size(); m++) {
                    long movieScore = Math.round(predictedMovieScores[u][m]);
                    scores[m + 1] = String.valueOf(movieScore);
                }
                csvWriter.writeNext(scores);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        logger.info("======== Finished exporting predicted scores =========");
    }

    public double computeRMSE() {
        logger.info("========= Computing root mean squared error of predictions ========");
        double sumOfSquaredErrors = 0;
        double numActualScores = 0;

        for(int u = 0; u < users.size(); u++) {
            for(int m = 0; m < movies.size(); m++) {
                double actualScore = actualMovieScores[u][m];
                if(actualScore > 0) {
                    double predictedScore = predictScore(u, m);
                    double error = actualScore - predictedScore;
                    sumOfSquaredErrors += Math.pow(error, 2);
                    numActualScores++;
                }
            }
        }

        double rootMeanSquaredError = Math.sqrt(sumOfSquaredErrors / numActualScores);

        logger.info("========= Predictions have an SSE of {} and an RMSE of {} =========",
                sumOfSquaredErrors, rootMeanSquaredError);

        return rootMeanSquaredError;
    }

    public void predictScores() {
        logger.info("========= Predicting missing scores =========");
        for(Prediction scoreToPredict: scoresToPredict) {
            User user = scoreToPredict.getUser();
            Movie movie = scoreToPredict.getMovie();
            int u = users.indexOf(user);
            int m = movies.indexOf(movie);
            double predictedScore = predictScore(user, movie);

            logger.info("Predicted score for user {} on movie {} is {}",
                    user.getId(), movie.getId(), predictedScore);

            predictedMovieScores[u][m] = predictedScore;
            scoreToPredict.setScore(predictedScore);
        }
    }

    public void savePredictions() {
        logger.info("========= Saving predictions to database =========");
        for(Prediction scoreToPredict: scoresToPredict) {
            rsDAO.save(scoreToPredict);
        }
    }

    public double predictScore(User user, Movie movie) {
        logger.info("Computing predicted score for user {} on movie {}",
                user.getId(), movie.getId());
        int userIndex = users.indexOf(user);
        int movieIndex = movies.indexOf(movie);

        if(userIndex > -1 && movieIndex > -1) {
            return predictScore(userIndex, movieIndex);
        }

        return 0;
    }

    public abstract double predictScore(int user, int movie);
}
