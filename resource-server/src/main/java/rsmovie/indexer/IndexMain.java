package rsmovie.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexMain {

    private static final Logger logger = LoggerFactory.getLogger(IndexMain.class);

    public static void main(String args[]) {
        logger.info("========= Starting to index movies =========");
        RsMovieIndexer indexer = new RsMovieIndexer();
        indexer.indexMovies();
        logger.info("========= Finished indexing movies =========");
    }
}
