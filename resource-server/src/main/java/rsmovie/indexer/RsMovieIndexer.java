package rsmovie.indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsmovie.dao.RsDAO;
import rsmovie.entity.Movie;
import rsmovie.entity.Review;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RsMovieIndexer {

    public static final String INDEX_DIRECTORY = System.getProperty("user.home") + "\\RS" + "\\Index";

    public static final String ID = "id";
    public static final String SCORE = "score";
    public static final String REVIEW_CONTENT = "review_content";

    private RsDAO rsDAO;

    private static final Logger logger = LoggerFactory.getLogger(RsMovieIndexer.class);

    public RsMovieIndexer() {
        rsDAO = RsDAO.getInstance();
        initializeIndexDirectory();
    }

    private void initializeIndexDirectory() {
        File dir = new File(INDEX_DIRECTORY);

        if(!dir.exists()) {
            if(dir.mkdir()) {
                logger.info("Created new directory {}", INDEX_DIRECTORY);
            } else {
                logger.info("Failed to create new directory {}", INDEX_DIRECTORY);
            }
        }
    }

    public void indexMovies() {
        IndexWriter writer = null;
        FSDirectory directory = null;

        try {
            logger.info("Opening index directory {}", INDEX_DIRECTORY);
            directory = FSDirectory.open(new File(INDEX_DIRECTORY).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
            writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            logger.info("Setting index writer");
            writer = new IndexWriter(directory, writerConfig);
            indexMovies(writer);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) {
                    writer.close();
                }
                if(directory != null) {
                    directory.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void indexMovies(IndexWriter writer) {
        logger.info("========= Indexing movies =========");
        List<Movie> movies = rsDAO.findAll(Movie.class);
        logger.info("Found {} movies to index", movies.size());
        for(Movie movie: movies) {
            indexMovie(writer, movie);
        }
    }

    private void indexMovie(IndexWriter writer, Movie movie) {
        logger.info("Beginning to index movie with ID {}", movie.getId());

        Document document = new Document();

        try {
            TextField id = new TextField(ID, movie.getId(), Field.Store.YES);
            document.add(id);
            logger.info("Added field '{}' to indexed movie document with value {}", ID, movie.getId());

            StoredField score = new StoredField(SCORE, movie.getScore());
            document.add(score);
            logger.info("Added field '{}' to indexed movie document with value {}", SCORE, movie.getScore());

            TextField content = new TextField(
                    REVIEW_CONTENT,
                    compileMovieReviewContent(movie),
                    Field.Store.YES
            );
            document.add(content);
            logger.info("Added field '{}' to indexed movie document", REVIEW_CONTENT);

            logger.info("Adding document to index writer");
            writer.addDocument(document);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String compileMovieReviewContent(Movie movie) {
        logger.info("Compiling review content for movie with ID {}", movie.getId());

        StringBuilder sb = new StringBuilder();

        List<Review> reviews = rsDAO.findReviewsByMovieOrUser(movie, Movie.class);

        logger.info("Found {} reviews for movie with ID {}", reviews.size(), movie.getId());

        for(Review review: reviews) {
            sb.append(review.getContent()).append(" ");
        }

        return sb.toString();
    }
}
