package rsmovie.prediction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UserBasedCollaborativeFiltering extends CollaborativeFiltering {

    private static final Logger logger = LoggerFactory.getLogger(
            UserBasedCollaborativeFiltering.class
    );

    public UserBasedCollaborativeFiltering() {
        super();
    }

    @Override
    public double computeSimilarity(int index1, int index2) {
        logger.info("========= Computing similarity between user {} and user {} =========",
                index1, index2);

        double covariance = 0;
        double user1Variance = 0;
        double user2Variance = 0;
        double totalCoRatedItems = 0;
        double thresholdValue = 50;

        String user1Id = users.get(index1).getId();
        String user2Id = users.get(index2).getId();

        double user1AverageScore = userAverageScores.get(user1Id);
        double user2AverageScore = userAverageScores.get(user2Id);

        for(int movie = 0; movie < movies.size(); movie++) {
            double user1Score = actualMovieScores[index1][movie];
            double user2Score = actualMovieScores[index2][movie];

            if(user1Score > 0 && user2Score > 0) {
                double user1Difference = user1Score - user1AverageScore;
                double user2Difference = user2Score - user2AverageScore;
                covariance += user1Difference * user2Difference;
                user1Variance += Math.pow(user1Difference, 2);
                user2Variance += Math.pow(user2Difference, 2);
                totalCoRatedItems++;
            }
        }

        double user1StandardDeviation = Math.sqrt(user1Variance);
        double user2StandardDeviation = Math.sqrt(user2Variance);

        double scaledSimilarity = -1;

        if(totalCoRatedItems > 0) {
            double similarity = covariance / (user1StandardDeviation * user2StandardDeviation);
            double scalingFactor = Math.min(totalCoRatedItems / thresholdValue, 1);
            scaledSimilarity = scalingFactor * similarity;
        }

        logger.info("========= User {} and user {} have a similarity of {} =========",
                index1, index2, scaledSimilarity);

        return scaledSimilarity;
    }

    @Override
    public double predictScore(int user, int movie) {
        logger.info("Predicting score for user {} on movie {}", user, movie);
        similarities = new HashMap<>();

        logger.info("Computing similarities between user {} and all other users that have " +
                "reviewed movie {}", user, movie);
        for(int u = 0; u < users.size(); u++) {
            if(u != user && actualMovieScores[u][movie] > 0) {
                similarities.put(u, computeSimilarity(user, u));
            }
        }

        logger.info("Similarity values computed for user {} and {} other users",
                user, similarities.size());

        similarities = sortSimilarities(similarities);

        String userId = users.get(user).getId();
        double userAverageScore = userAverageScores.get(userId);

        double weightedTotal = 0;
        double similarityTotal = 0;

        double numNeighboursIncluded = 0;

        logger.info("Computing predicted score using a neighbourhood of at most {} neighbours",
                maxNeighboursToInclude);
        for(Map.Entry<Integer, Double> similarity: similarities.entrySet()) {
            if(similarity.getValue() > 0 && numNeighboursIncluded < maxNeighboursToInclude) {
                numNeighboursIncluded++;

                int userIndex = similarity.getKey();
                String id = users.get(userIndex).getId();

                double averageScore = userAverageScores.get(id);
                double score = actualMovieScores[userIndex][movie];
                double similarityValue = similarity.getValue();

                weightedTotal += similarityValue * (score - averageScore);
                similarityTotal += similarityValue;
            }
        }

        double weightedAverage = weightedTotal / similarityTotal;

        double predictedScore = movies.get(movie).getScore();

        if(numNeighboursIncluded > 0) {
            predictedScore = userAverageScore + weightedAverage;
        }

        logger.info("========= Predicted score for user {} on movie {} is {} =========",
                user, movie, predictedScore);

        return predictedScore;
    }
}
