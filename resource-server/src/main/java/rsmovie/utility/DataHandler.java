package rsmovie.utility;

import com.opencsv.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsmovie.dao.RsDAO;
import rsmovie.entity.*;
import rsmovie.service.HttpService;
import rsmovie.service.impl.HttpServiceImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Map.Entry.comparingByValue;

public class DataHandler {

    private static final Logger logger = LoggerFactory.getLogger(DataHandler.class);

    public static final String DATA_DIRECTORY = "src/data";
    public static final String REVIEWS_DIRECTORY =
            DATA_DIRECTORY + "/reviews";
    public static final String TOPICS_DIRECTORY = DATA_DIRECTORY + "/topics";


    private RsDAO rsDAO;
    private HttpService httpService;
    private HashMap<String, User> users;
    private HashMap<String, Movie> movies;
    private List<Review> reviews;
    private List<Topic> topics;
    private List<Term> terms;
    private Map<Long, Community> communities;
    private List<String[]> topicsToTerms;
    private List<String[]> moviesToTopics;

    public DataHandler() {
        rsDAO = RsDAO.getInstance();
        httpService = new HttpServiceImpl();
    }

    public void importReviews() {
        logger.info("========== Starting to import reviews =========");
        movies = new HashMap<>();
        users = new HashMap<>();
        reviews = new ArrayList<>();
        File reviewFiles = new File(REVIEWS_DIRECTORY);
        for(File reviewFile: Objects.requireNonNull(reviewFiles.listFiles())) {
            String fileName = reviewFile.getName()
                    .substring(0, reviewFile.getName().length() - 5);
            String[] ids = fileName.split("-");
            String userId = ids[0];
            String movieId = ids[1];
            logger.info("Imported review of movie {} from user {}", movieId, userId);

            User user = null;
            if(!users.containsKey(userId)) {
                logger.info("Created new user with ID {}", userId);
                user = new User();
                user.setId(userId);
                user.setUsername("user" +users.size() + 1);
                users.put(userId, user);
                httpService.registerUser(user, "password");
            } else {
                logger.info("Found user with ID {}", userId);
                user = users.get(userId);
            }

            Movie movie = null;
            if(!movies.containsKey(movieId)) {
                logger.info("Created new movie with ID {}", movieId);
                movie = new Movie();
                movie.setId(movieId);
                movies.put(movieId, movie);
            } else {
                logger.info("Found movie with ID {}", movieId);
                movie = movies.get(movieId);
            }

            Review review = parseReview(reviewFile);
            user.addReview(review);
            movie.addReview(review);
            reviews.add(review);
        }

        saveData();
    }

    public Review parseReview(File reviewFile) {
        logger.info("Parsing review");
        Review review = new Review();
        try {
            Document document = Jsoup.parse(reviewFile, "UTF-8");

            Elements metadata = document.select("meta");
            for(Element meta: metadata) {

                switch(meta.attr("name")) {
                    case "score":
                        Double score = Double.parseDouble(meta.attr("content"));
                        review.setScore(Math.round(score));
                        break;
                    case "summary":
                        String summary = meta.attr("content");
                        review.setSummary(summary);
                        break;
                    default:
                        break;
                }
            }

            Element content = document.selectFirst("p");
            review.setContent(content.text());
        } catch(IOException e) {
            e.printStackTrace();
        }
        return review;
    }

    private void saveData() {
        for(User user: users.values()) {
            rsDAO.save(user);
        }

        for(Movie movie: movies.values()) {
            rsDAO.save(movie);
        }

        for(Review review: reviews) {
            rsDAO.save(review);
        }
    }

    public void scoreMovies() {
        logger.info("========== Scoring Movies ==========");
        List<Movie> movies = rsDAO.findAll(Movie.class);
        logger.info("{} movies found", movies.size());
        for(Movie movie: movies) {
            logger.info("========= Scoring movie with ID: {} ==========", movie.getId());
            List<Review> reviews = rsDAO.findReviewsByMovieOrUser(movie, Movie.class);
            logger.info("{} reviews found", reviews.size());
            double totalScore = 0;
            for(Review review: reviews) {
                totalScore+= review.getScore();
            }
            Double finalScore = totalScore / reviews.size();
            logger.info("{} revieved a total score of {} and a final score of {}",
                    movie.getId(), totalScore, finalScore);
            movie.setScore(finalScore);
            rsDAO.save(movie);
        }
        logger.info("========= Finished scoring movies =========");
    }

    public void computeUsersAverageScore() {
        logger.info("========= Computing average scores for each user ==========");
        List<User> users = rsDAO.findAll(User.class);
        logger.info("{} users found", users.size());
        for(User user: users) {
            logger.info("========= Computing average score for user with ID: {} =========",
                    user.getId());
            List<Review> reviews = rsDAO.findReviewsByMovieOrUser(user, User.class);
            logger.info("{} reviews found", reviews.size());
            double totalScore = 0;
            for(Review review: reviews) {
                totalScore += review.getScore();
            }
            Double avgScore = totalScore / reviews.size();
            logger.info("{} has a total score of {} and an average score of {}",
                    user.getId(), totalScore, avgScore);
            user.setAvgScore(avgScore);
            rsDAO.save(user);
        }
        logger.info("========= Finished computing average user scores =========");
    }

    public void exportScores() {
        logger.info("========= Exporting user scores to CSV file =========");
        Writer writer;
        try {
            writer = new FileWriter(DATA_DIRECTORY + "/scores.csv");
            CSVWriter csvWriter = new CSVWriter(writer);
            List<User> users = rsDAO.findAll(User.class);
            List<Movie> movies = rsDAO.findAll(Movie.class);

            String[] movieIds = new String[movies.size() + 1];
            movieIds[0] = "";
            for(int m = 0; m < movies.size(); m++) {
                movieIds[m + 1] = movies.get(m).getId();
            }
            csvWriter.writeNext(movieIds);

            for(int u = 0; u < users.size(); u++) {
                String[] movieScores = new String[movies.size() + 1];
                movieScores[0] = users.get(u).getId();
                for(int m = 0; m < movies.size(); m++) {
                    Review review = rsDAO.findReviewByMovieAndUser(movies.get(m), users.get(u));
                    long movieScore = 0;
                    if(review != null) {
                        movieScore = review.getScore();
                    }
                    movieScores[m + 1] = String.valueOf(movieScore);
                }
                csvWriter.writeNext(movieScores);
            }
        } catch(IOException e ) {
            e.printStackTrace();
        }
        logger.info("========= Finished exporting scores =========");
    }

    public void exportReviewsToText() {
        logger.info("========= Exporting review content to text files ==========");

        logger.info("Fetching movies from database");
        List<Movie> movies = rsDAO.findAll(Movie.class);
        logger.info("Found {} movies", movies.size());

        for(Movie movie: movies) {
            logger.info("Exporting reviews for movie {}", movie.getId());

            String movieId = movie.getId();

            logger.info("Fetching reviews for movie {}", movie.getId());
            List<Review> reviews = rsDAO.findReviewsByMovieOrUser(movie, Movie.class);
            logger.info("Found {} reviews", reviews.size());

            try {
                logger.info("Exporting reviews to {}.txt", movie.getId());
                FileWriter writer = new FileWriter(REVIEWS_DIRECTORY + "/txt/" + movieId + ".txt");
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                for(Review review: reviews) {
                    logger.info("Exporting review {}", review.getId());
                    bufferedWriter.write(review.getContent());
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("========= Finished exporting reviews =========");
    }

    public void importTopicsAndTerms() {
        logger.info("========= Importing and processing topics and terms produced by LDA analysis ========");

        try {
            Reader reader = Files.newBufferedReader(Paths.get(TOPICS_DIRECTORY +
                    "/LDA Gibbs 25 TopicsToTerms.csv"));
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(true)
                    .build();
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(parser)
                    .build();
            topicsToTerms = csvReader.readAll();

            reader = Files.newBufferedReader(Paths.get(TOPICS_DIRECTORY +
                    "/LDA Gibbs 25 DocsToTopics.csv"));
            csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();
            moviesToTopics = csvReader.readAll();

            processTopicsAndTerms();
            processMoviesToTopics();
            saveTopics();
        } catch(IOException e) {
            e.printStackTrace();
        }

        logger.info("Finished importing and processing topics and terms");
    }

    private void processTopicsAndTerms() {
        logger.info("Processing topics and terms");

        topics = new ArrayList<>();
        terms = new ArrayList<>();

        String[] topicRow = topicsToTerms.get(0);
        for(int t = 1; t < topicRow.length; t++) {
            logger.info("Adding new topic");
            Topic topic = new Topic();
            topics.add(topic);
        }

        for(int row = 1; row < topicsToTerms.size(); row++) {
            String[] termRow = topicsToTerms.get(row);
            long termRank = 1;

            logger.info("Adding terms with rank {}", termRank);
            for(int t = 1; t < termRow.length; t++) {
                Term term = new Term();
                term.setTermRank(termRank);
                term.setValue(termRow[t]);
                logger.info("Adding new term with rank {} and value {}", termRank, termRow[t]);
                terms.add(term);
                Topic topic = topics.get(t - 1);
                topic.addTerm(term);
            }

            termRank++;
        }

        logger.info("Finished processing topics and terms");
    }

    private void processMoviesToTopics() {
        logger.info("Assigning topics to movies");

        for(String[] movieToTopic: moviesToTopics) {
            String movieId = movieToTopic[0].substring(0, movieToTopic[0].length() - 4);
            int topicIndex = Integer.parseInt(movieToTopic[1]);
            Movie movie = rsDAO.find(movieId, Movie.class);
            Topic topic = topics.get(topicIndex - 1);
            topic.addMovie(movie);
        }

        logger.info("Finished assigning topics to movies");
    }

    private void saveTopics() {
        for(Topic topic: topics) {
            rsDAO.save(topic);
        }
    }

    public void exportTopicScores() {
        logger.info("========= Exporting user scores for each topic =========");

        try {
            Writer writer = new FileWriter(TOPICS_DIRECTORY + "/user-topic-scores.csv");
            CSVWriter csvWriter = new CSVWriter(writer);

            logger.info("Fetching all topics {}");
            List<Topic> topics = rsDAO.findAll(Topic.class);
            logger.info("Found {} topics", topics.size());

            String[] topicsIds = new String[topics.size() + 1];
            topicsIds[0] = "";

            logger.info("Gathering topics IDs");
            for(int t = 0; t < topics.size(); t++) {
                Topic topic = topics.get(t);
                topicsIds[t + 1] = String.valueOf(topic.getId());
            }
            logger.info("Writing topics IDs to CSV");
            csvWriter.writeNext(topicsIds);

            logger.info("Fetching all users");
            List<User> users = rsDAO.findAll(User.class);
            logger.info("Found {} users", users.size());

            for(User user : users) {
                logger.info("Compiling topic scores for user {}", user.getId());

                String[] userRow = new String[topics.size() + 1];
                userRow[0] = user.getId();

                for(int t = 0; t < topics.size(); t++) {
                    Topic topic = topics.get(t);

                    logger.info("Fetching all movies with topic {}", topic.getId());
                    List<Movie> movies = rsDAO.findMoviesByTopic(topic);
                    logger.info("Found {} movies", movies.size());

                    double totalScore = 0;
                    double numReviews = 0;
                    for(Movie movie: movies) {
                        logger.info("Fetching review by user {} on movie {}", user.getId(), movie.getId());
                        Review review = rsDAO.findReviewByMovieAndUser(movie, user);
                        if(review != null) {
                            logger.info("Review found with a score of {}", review.getScore());
                            totalScore += review.getScore();
                            numReviews++;
                        }
                    }

                    double avgScore = 0;

                    if(numReviews > 0) {
                        avgScore = totalScore / numReviews;
                    }

                    logger.info("Average score by user {} on movies from topic {} is",
                            user.getId(), topic.getId(), avgScore);

                    long roundedScore = Math.round(avgScore);

                    userRow[t + 1] = String.valueOf(roundedScore);
                }

                csvWriter.writeNext(userRow);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        logger.info("======== Finished exporting user topic scores =========");
    }

    public void assignTopicsToUsers() {
        logger.info("======== Assigning top 5 topics to each user =========");

        try {
            Reader reader = Files.newBufferedReader(Paths.get(TOPICS_DIRECTORY +
                    "/user-topic-scores.csv"));
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(true)
                    .build();
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(parser)
                    .build();
            List<String[]> topicScores = csvReader.readAll();

            String[] topicIds = topicScores.get(0);

            for(int row = 1; row < topicScores.size(); row++) {
                String[] userRow = topicScores.get(row);
                String userId = userRow[0];

                logger.info("Getting topic scores for user {}", userId);

                Map<String, Double> userScores = new HashMap<>();

                for(int score = 1; score < userRow.length; score++) {
                    String topicId = topicIds[score];
                    double topicScore = Double.parseDouble(userRow[score]);
                    logger.info("User {} have a score of {} to topic {}",
                            userId, topicScore, topicId);
                    userScores.put(topicId, topicScore);
                }

                logger.info("Sorting topic scores for user {}", userId);
                userScores = sortMap(userScores);

                logger.info("Fetching user {}", userId);
                User user = rsDAO.find(userId, User.class);

                int numTopics = 0;
                logger.info("Assigning topics to user {}", user.getId());
                for(Map.Entry<String, Double> entry: userScores.entrySet()) {
                    if(numTopics < 5) {
                        numTopics++;
                        Topic topic = rsDAO.find(entry.getKey(), Topic.class);
                        user.addTopic(topic);
                        topic.addUser(user);
                        rsDAO.save(topic);
                    }
                }

                rsDAO.save(user);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        logger.info("Finished assigning topics to users");
    }

    public void assignUsersToCommunities() {
        logger.info("======== Assigning users to a community of similar users ========");

        try {
            Reader reader = Files.newBufferedReader(Paths.get(TOPICS_DIRECTORY +
                    "/KMeans 3 Clusters.csv"));
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(true)
                    .build();
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();
            List<String[]> rows = csvReader.readAll();

            communities = new HashMap<>();

            for(String[] row: rows) {
                String userId = row[0];

                logger.info("Fetching user {}", userId);
                User user = rsDAO.find(userId, User.class);

                Long communityNumber = Long.parseLong(row[1]);

                logger.info("Assigning user to community {}", communityNumber);
                if(!communities.containsKey(communityNumber)) {
                    logger.info("Creating community {}", communityNumber);
                    Community community = new Community();
                    community.setCommunityNumber(communityNumber);
                    community.addUser(user);
                    communities.put(communityNumber, community);
                } else {
                    Community community = communities.get(communityNumber);
                    community.addUser(user);
                }
            }

            for(Community community: communities.values()) {
                rsDAO.save(community);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        logger.info("======== Finished assigning users to communities =========");
    }

    public void assignTopicsToCommunities() {
        List<Community> communities = rsDAO.findAll(Community.class);

        for(Community community: communities) {
            List<User> users = rsDAO.findUsersByCommunity(community);
            Map<Topic, Integer> topicCount = new HashMap<>();

            for(User user: users) {
                Set<Topic> topics = user.getTopics();

                for(Topic topic: topics) {
                    if(!topicCount.containsKey(topic)) {
                        topicCount.put(topic, 1);
                    } else {
                        topicCount.put(topic, topicCount.get(topic) + 1);
                    }
                }
            }

            topicCount = sortMap(topicCount);

            int numTopics = 0;
            for(Map.Entry<Topic, Integer> entry: topicCount.entrySet()) {
                if(numTopics < 10) {
                    numTopics++;
                    Topic topic = entry.getKey();
                    community.addTopic(topic);
                    topic.addCommunity(community);
                    rsDAO.save(topic);
                }
            }

            rsDAO.save(community);
        }
    }

    private <T, S extends Comparable<? super S>> Map<T, S> sortMap(Map<T, S> map) {
        List<Map.Entry<T, S>> entries = new LinkedList<>(map.entrySet());
        entries.sort(Map.Entry.comparingByValue());

        Map<T, S> sortedMap = new LinkedHashMap<>();
        for(Map.Entry<T, S> entry: entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}