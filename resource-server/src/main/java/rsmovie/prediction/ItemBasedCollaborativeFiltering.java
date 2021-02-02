package rsmovie.prediction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ItemBasedCollaborativeFiltering extends CollaborativeFiltering {

    private static final Logger logger = LoggerFactory.getLogger(
            ItemBasedCollaborativeFiltering.class
    );

    public ItemBasedCollaborativeFiltering() {
        super();
    }

    @Override
    public double computeSimilarity(int index1, int index2) {
        logger.info("========= Computing similarity between movie {} and movie {}",
                index1, index2);

        double movieVectorCrossProduct = 0;
        double movie1VectorLength = 0;
        double movie2VectorLength = 0;

        for(int user = 0; user < users.size(); user++) {
            double movie1Score = actualMovieScores[user][index1];
            double movie2Score = actualMovieScores[user][index2];

            if(movie1Score > 0 && movie2Score > 0) {
                double userAvgScore = users.get(user).getAvgScore();
                double movie1Difference = movie1Score - userAvgScore;
                double movie2Difference = movie2Score - userAvgScore;
                movieVectorCrossProduct += movie1Difference * movie2Difference;
                movie1VectorLength += Math.pow(movie1Difference, 2);
                movie2VectorLength += Math.pow(movie2Difference, 2);
            }
        }

        movie1VectorLength = Math.sqrt(movie1VectorLength);
        movie2VectorLength = Math.sqrt(movie2VectorLength);

        double similarity = movieVectorCrossProduct / (movie1VectorLength * movie2VectorLength);

        logger.info("========= Movie {} and movie {} have a similarity of {} =========",
                index1, index2, similarity);

        return similarity;

    }

    @Override
    public double predictScore(int user, int movie) {
        logger.info("Predicting score for user {} on movie {}", user, movie);
        similarities = new HashMap<>();

        logger.info("Computing similarities between movie {} and all other movies that user {}" +
                " has reviewed", movie, user);
        for(int m = 0; m < movies.size(); m++) {
            if(m != movie && actualMovieScores[user][m] > 0) {
                similarities.put(m, computeSimilarity(movie, m));
            }
        }

        logger.info("Similarity values computed for movie {} and {} other movies",
                movie, similarities.size());

        similarities = sortSimilarities(similarities);

        double weightedTotal = 0;
        double similarityTotal = 0;

        double numNeighboursIncluded = 0;

        logger.info("Computing predicted score using a neighbourhood of at most {} neighbours",
                maxNeighboursToInclude);
        for(Map.Entry<Integer, Double> similarity: similarities.entrySet()) {
            if(similarity.getValue() > 0 && numNeighboursIncluded < maxNeighboursToInclude) {
                numNeighboursIncluded++;

                int movieIndex = similarity.getKey();

                double score = actualMovieScores[user][movieIndex];
                double similarityValue = similarity.getValue();

                weightedTotal += similarityValue * score;
                similarityTotal += similarityValue;
            }
        }

        double predictedScore = movies.get(movie).getScore();

        if(numNeighboursIncluded > 0) {
            predictedScore = weightedTotal / similarityTotal;
        }

        logger.info("========= Predicted score for user {} on movie {} is {} =========",
                user, movie, predictedScore);

        return predictedScore;
    }
}
