package rsmovie.prediction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsmovie.entity.Movie;
import rsmovie.entity.User;

import java.util.*;

public abstract class CollaborativeFiltering extends ScorePrediction {

    private static final Logger logger = LoggerFactory.getLogger(CollaborativeFiltering.class);

    protected Map<Integer, Double> similarities;

    protected final double maxNeighboursToInclude = Math.ceil(
            Math.log(users.size()) / Math.log(2)
    );

    public CollaborativeFiltering() {
        super();
    }

    protected Map<Integer, Double> sortSimilarities(Map<Integer, Double> similarities) {
        logger.info("======== Sorting map of similarities ========");
        List<Map.Entry<Integer, Double>> entries = new ArrayList<>(similarities.entrySet());
        Collections.sort(entries, (o1, o2) -> (o2.getValue().compareTo(o1.getValue())));
        Map<Integer, Double> sortedSimilarities = new HashMap<>();
        for(Map.Entry<Integer, Double> entry: entries) {
            sortedSimilarities.put(entry.getKey(), entry.getValue());
        }
        logger.info("======== Finished sorting map ========");
        return sortedSimilarities;
    }

    protected abstract double computeSimilarity(int index1, int index2);

    public abstract double predictScore(int user, int movie);
}
