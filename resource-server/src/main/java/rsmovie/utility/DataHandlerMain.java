package rsmovie.utility;

public class DataHandlerMain {

    public static void main(String[] args) {
        DataHandler dataHandler = new DataHandler();
        dataHandler.importReviews();
        dataHandler.scoreMovies();
        dataHandler.computeUsersAverageScore();
        dataHandler.exportScores();
        dataHandler.exportReviewsToText();
        dataHandler.importTopicsAndTerms();
        dataHandler.exportTopicScores();
        dataHandler.assignTopicsToUsers();
        dataHandler.assignUsersToCommunities();
        dataHandler.assignTopicsToCommunities();
    }
}
