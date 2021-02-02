package rsmovie.prediction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PredictionMain {

    private static final Logger logger = LoggerFactory.getLogger(PredictionMain.class);

    public static void main(String args[]) {
        logger.info("========= Using item based collaborative filtering to predict scores =========");
        ScorePrediction scorePrediction = new ItemBasedCollaborativeFiltering();
        scorePrediction.predictScores();
        double rmse = scorePrediction.computeRMSE();
        logger.info("Item based CF has an RMSE of {}", rmse);
        scorePrediction.exportScores("item-based-cf");
        scorePrediction.savePredictions();
        logger.info("========== Finished using item based CF for predictions ==========");

        logger.info("========= Using user based collaborative filtering to predict scores =========");
        scorePrediction = new UserBasedCollaborativeFiltering();
        scorePrediction.predictScores();
        rmse = scorePrediction.computeRMSE();
        logger.info("User based CF has an RMSE of {}", rmse);
        scorePrediction.exportScores("user-based-cf");
        logger.info("========== Finished using user based CF for predictions ==========");

        logger.info("========= Using SVD with score imputation to predict scores =========");
        scorePrediction = new SVDUsingImputation();
        scorePrediction.predictScores();
        rmse = scorePrediction.computeRMSE();
        logger.info("SVD with imputation has an RMSE of {}", rmse);
        scorePrediction.exportScores("svd-imputation");
        logger.info("========== Finished using SVD with imputation for predictions ==========");

        logger.info("========= Using SVD with gradient descent to predict scores =========");
        scorePrediction = new SVDUsingGradientDescent();
        scorePrediction.predictScores();
        rmse = scorePrediction.computeRMSE();
        logger.info("SVD with gradient descent has an RMSE of {}", rmse);
        scorePrediction.exportScores("svd-gradient-descent");
        logger.info("========== Finished using SVD with GD for predictions ==========");

    }
}
