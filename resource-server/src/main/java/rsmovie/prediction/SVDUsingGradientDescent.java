package rsmovie.prediction;

import Jama.Matrix;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SVDUsingGradientDescent extends SVD {

    private static final Logger logger = LoggerFactory.getLogger(SVDUsingGradientDescent.class);

    private double learningRate = 0.001;
    private double regularizationFactor = 0.1;

    SVDUsingGradientDescent() {
        super();
        computeSVD(new Matrix(actualMovieScores));
        adjustMatricesUsingGradientDescent();
    }

    private void adjustMatricesUsingGradientDescent() {
        logger.info("========= Adjusting SVD matrices using gradient descent =========");

        int maxIterations = 25;
        int numIterations = 0;

        double rmse = computeRMSE();

        logger.info("Matrix U has dimensions {} x {}",
                U.getRowDimension(), U.getColumnDimension());
        logger.info("Matrix M has dimensions {} x {}",
                M.getRowDimension(), M.getColumnDimension());

        logger.info("RMSE is {} before gradient descent", rmse);

        while(numIterations < maxIterations) {
            numIterations++;
            List<GradientScorePrediction> gradientScores = predictScoresForGradient();
            U = U.minus(computeUserGradient(gradientScores));
            M = M.minus(computeMovieGradient(gradientScores));
            rmse = computeRMSEForGradient(gradientScores);
            logger.info("RMSE is {} in iteration {}", rmse, numIterations);
        }

        logger.info("======== Finished gradient descent =========");
    }

    private List<GradientScorePrediction> predictScoresForGradient() {
        List<GradientScorePrediction> gradientScores = new ArrayList<>();

        for(int user = 0; user < users.size(); user++) {
            for(int movie = 0; movie < movies.size(); movie++) {
                double actualScore = actualMovieScores[user][movie];
                if(actualScore > 0) {
                    double u = Math.random();
                    if(u < 0.3) {
                        double predictedScore = predictScore(user, movie);
                        GradientScorePrediction gradientScore = new GradientScorePrediction();
                        gradientScore.setActualScore(actualScore);
                        gradientScore.setPredictedScore(predictedScore);
                        Matrix userVector = U.getMatrix(
                                user, user, 0, U.getColumnDimension() - 1
                        );
                        Matrix movieVector = M.getMatrix(
                                0, M.getRowDimension() - 1, movie, movie
                        );
                        gradientScore.setUserVector(userVector);
                        gradientScore.setMovieVector(movieVector);
                        gradientScores.add(gradientScore);
                    }
                }
            }
        }

        return gradientScores;
    }

    private Matrix computeUserGradient(List<GradientScorePrediction> gradientScores) {
        logger.info("Computing gradient for user factor matrix U");

        double[][] gradient = new double[U.getRowDimension()][U.getColumnDimension()];

        for(int row = 0; row < U.getRowDimension(); row++) {
            for(int col = 0; col < U.getColumnDimension(); col++) {
                double gradientEntry = 0;

                for(GradientScorePrediction gradientScore: gradientScores) {
                    double movieVectorEntry = gradientScore.getMovieVector().get(col, 0);
                    double error = -2.0 * (
                            gradientScore.getActualScore() - gradientScore.getPredictedScore()
                    ) * movieVectorEntry;
                    gradientEntry += error;
                }

                gradientEntry = gradientEntry / gradientScores.size();

                double userMatrixEntry = U.get(row, col);
                double regularizationTerm = 2.0 * regularizationFactor * userMatrixEntry;
                gradient[row][col] = gradientEntry + regularizationTerm;
            }
        }

        logger.info("Finished computing user gradient");

        Matrix gradientMatrix = new Matrix(gradient);
        gradientMatrix = gradientMatrix.times(learningRate);

        return gradientMatrix;
    }

    private Matrix computeMovieGradient(List<GradientScorePrediction> gradientScores) {
        logger.info("Computing gradient for movie factor matrix");

        double[][] gradient = new double[M.getRowDimension()][M.getColumnDimension()];

        for(int row = 0; row < M.getRowDimension(); row++) {
            for(int col = 0; col < M.getColumnDimension(); col++) {
                double gradientEntry = 0;

                for(GradientScorePrediction gradientScore: gradientScores) {
                    double userVectorEntry = gradientScore.getUserVector().get(0, row);
                    double error = -2.0 * (
                            gradientScore.getActualScore() - gradientScore.getPredictedScore()
                    ) * userVectorEntry;
                    gradientEntry += error;
                }

                gradientEntry = gradientEntry / gradientScores.size();

                double movieMatrixEntry = M.get(row, col);
                double regularizationTerm = 2.0 * regularizationFactor * movieMatrixEntry;
                gradient[row][col] = gradientEntry + regularizationTerm;
            }
        }

        logger.info("Finished computing movie gradient");

        Matrix gradientMatrix = new Matrix(gradient);
        gradientMatrix = gradientMatrix.times(learningRate);

        return gradientMatrix;
    }

    private double computeRMSEForGradient(List<GradientScorePrediction> gradientScores) {
        double sumOfSquaredErrors = 0;

        for(GradientScorePrediction gradientScore: gradientScores) {
            double error = gradientScore.getActualScore() - gradientScore.getPredictedScore();
            sumOfSquaredErrors += Math.pow(error, 2);
        }

        return Math.sqrt(sumOfSquaredErrors / gradientScores.size());
    }

    @Data
    private class GradientScorePrediction {

        private double actualScore;
        private double predictedScore;
        private Matrix userVector;
        private Matrix movieVector;
    }
}
