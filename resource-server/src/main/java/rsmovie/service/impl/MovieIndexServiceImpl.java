package rsmovie.service.impl;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rsmovie.dao.MovieRepository;
import rsmovie.entity.Movie;
import rsmovie.indexer.RsMovieIndexer;
import rsmovie.service.MovieIndexService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieIndexServiceImpl implements MovieIndexService {

    private static final Logger logger = LoggerFactory.getLogger(MovieIndexServiceImpl.class);

    private MovieRepository movieRepository;

    @Autowired
    public MovieIndexServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Page<Movie> searchMovies(String query, Pageable pageable) {
        logger.info("========= Starting search using query: {}", query);

        if(query.isEmpty()) {
            return movieRepository.findAll(pageable);
        }

        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(
                    new File(RsMovieIndexer.INDEX_DIRECTORY).toPath()
            ));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();

            QueryParser scoreParser = new QueryParser(RsMovieIndexer.SCORE, analyzer);
            Query scoreQuery = scoreParser.parse(query);

            QueryParser contentParser = new QueryParser(RsMovieIndexer.REVIEW_CONTENT, analyzer);
            Query contentQuery = contentParser.parse(query);

            Query finalQuery = new BooleanQuery.Builder()
                    .add(scoreQuery, BooleanClause.Occur.SHOULD)
                    .add(contentQuery, BooleanClause.Occur.MUST)
                    .build();

            TopDocs topDocs = searcher.search(finalQuery, 100);
            ScoreDoc[] hits = topDocs.scoreDocs;
            logger.info("Search returned {} documents", hits.length);
            return movieListToPage(getMovies(searcher, hits), pageable);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Movie> getMovies(IndexSearcher searcher, ScoreDoc[] hits) {
        List<Movie> movies = new ArrayList<>();

        for(ScoreDoc hit: hits) {
            Document movieDocument;

            try {
                movieDocument = searcher.doc(hit.doc);
                logger.info("Getting movie from database with ID {}",
                        movieDocument.get(RsMovieIndexer.ID));
                Optional<Movie> result = movieRepository.findById(movieDocument.get(RsMovieIndexer.ID));
                Movie movie;
                if(result.isPresent()) {
                    movie = result.get();
                    movies.add(movie);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        return movies;
    }

    private Page<Movie> movieListToPage(List<Movie> movies, Pageable pageable) {
        logger.info("Converting returned movie list to page");
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), movies.size());
        return new PageImpl<>(
                movies.subList(start, end), pageable, movies.size()
        );
    }
}
