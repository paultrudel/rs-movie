package rsmovie.prediction;

import Jama.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsmovie.entity.Movie;
import rsmovie.entity.Prediction;
import rsmovie.entity.User;

public class SVDUsingImputation extends SVD {

    private static final Logger logger = LoggerFactory.getLogger(SVDUsingImputation.class);

    private double[][] completeScoreMatrix;

    public SVDUsingImputation() {
        super();
        constructCompleteScoreMatrix();
        computeSVD(new Matrix(completeScoreMatrix));
    }

    private void constructCompleteScoreMatrix() {
        logger.info("========= Constructing filled score matrix for SVD =========");
        completeScoreMatrix = new double[users.size()][movies.size()];

        for(int user = 0; user < users.size(); user++) {
            for(int movie = 0; movie < movies.size(); movie++) {
                completeScoreMatrix[user][movie] = actualMovieScores[user][movie];
            }
        }

        imputeMissingScores();

        logger.info("========= Finished constructing matrix =========");
    }

    private void imputeMissingScores() {
        logger.info("========= Imputing missing scores to fill matrix =========");

        for(Prediction scoreToPredict: scoresToPredict) {
            User user = scoreToPredict.getUser();
            Movie movie = scoreToPredict.getMovie();

            logger.info("Imputing score for user {} on movie {}", user.getId(), movie.getId());

            int userIndex = users.indexOf(user);
            int movieIndex = movies.indexOf(movie);

            logger.info("Setting missing score to {} (the average score for movie {})",
                    movie.getScore(), movie.getId());
            completeScoreMatrix[userIndex][movieIndex] = movie.getScore();
        }

        logger.info("========= Finished imputing missing scores ========");
    }
}
