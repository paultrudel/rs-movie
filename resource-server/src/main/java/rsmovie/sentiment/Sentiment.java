package rsmovie.sentiment;

public class Sentiment {

    public static enum SentimentType {
        VERY_POSITIVE(10, 8, 10, 5),
        POSITIVE(7, 6, 8, 4),
        NEUTRAL(5, 4, 6, 3),
        NEGATIVE(3, 2, 4, 2),
        VERY_NEGATIVE(1, 0, 2, 1);

        private final int value;
        private final int minScore;
        private final int maxScore;
        private final int rating;

        private SentimentType(
                int value,
                int minScore,
                int maxScore,
                int rating
        ) {
            this.value = value;
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.rating = rating;
        }

        public int getValue() { return value; }
        public int getMinScore() { return minScore; }
        public int getMaxScore() { return maxScore; }
        public int getRating() { return rating; }
    }
}
