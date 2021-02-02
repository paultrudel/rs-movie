package rsmovie.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsmovie.entity.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RsDAO {

    private static final Logger logger = LoggerFactory.getLogger(RsDAO.class);

    private SessionFactory sessionFactory;
    private static RsDAO instance;

    private RsDAO() {
        openConnection();
    }

    private void openConnection() {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Community.class)
                .addAnnotatedClass(Movie.class)
                .addAnnotatedClass(Prediction.class)
                .addAnnotatedClass(Review.class)
                .addAnnotatedClass(Term.class)
                .addAnnotatedClass(Topic.class)
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
    }

    public static RsDAO getInstance() {
        if(instance == null) {
            instance = new RsDAO();
        }
        return instance;
    }

    public <T> void save(T entity) {
        logger.info("Attempting to save entity {}", entity);
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            transaction.commit();
        } catch(Exception e) {
            if(transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }
    }

    public <T> T find(String id, Class<T> entityClass) {
        logger.info("Attempting to find {} with ID {}", entityClass.getSimpleName(), id);
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        T result = null;

        try {
            transaction = session.beginTransaction();
            switch(entityClass.getSimpleName()) {
                case "Movie":
                case "User":
                    result = session.find(entityClass, id);
                    break;
                default:
                    result = session.find(entityClass, Long.parseLong(id));
                    break;
            }
            transaction.commit();
        } catch(Exception e) {
            if(transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }

        return result;
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        logger.info("Attempting to fetch all {}", entityClass.getSimpleName());
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<T> result = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            Root<T> root = criteriaQuery.from(entityClass);
            CriteriaQuery<T> allEntities = criteriaQuery.select(root);
            result = session.createQuery(allEntities).getResultList();
        } catch(Exception e) {
            if(transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }

        return result;
    }

    public <T> List<Review> findReviewsByMovieOrUser(T relatedEntity, Class<T> relatedEntityClass) {
        logger.info("Attempting to find reviews by {} with attributes {}",
                relatedEntityClass.getSimpleName(), relatedEntity);
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<Review> result = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Review> criteriaQuery = criteriaBuilder.createQuery(Review.class);
            Root<Review> reviewRoot = criteriaQuery.from(Review.class);
            criteriaQuery.where(
                    criteriaBuilder.equal(
                            reviewRoot.get(relatedEntityClass.getSimpleName().toLowerCase()),
                            relatedEntity
                    )
            );
            result = session.createQuery(criteriaQuery).getResultList();
            transaction.commit();
        } catch(Exception e) {
            if (transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }
        return result;
    }

    public Review findReviewByMovieAndUser(Movie movie, User user) {
        logger.info("Attempting to find review for movie {} by user {}", movie.getId(), user.getId());
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        Review result = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Review> criteriaQuery = criteriaBuilder.createQuery(Review.class);
            Root<Review> reviewRoot = criteriaQuery.from(Review.class);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(reviewRoot.get("movie"), movie));
            predicates.add(criteriaBuilder.equal(reviewRoot.get("user"), user));
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
            result = session.createQuery(criteriaQuery).getSingleResult();
            transaction.commit();
        } catch(Exception e) {
            if (transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }
        return result;
    }

    public List<Movie> findMoviesByTopic(Topic topic) {
        logger.info("Attempting to find movies for topic {}", topic.getId());

        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<Movie> result = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> criteriaQuery = criteriaBuilder.createQuery(Movie.class);
            Root<Movie> movieRoot = criteriaQuery.from(Movie.class);
            criteriaQuery.where(
                    criteriaBuilder.equal(
                            movieRoot.get("topic"),
                            topic
                    )
            );
            result = session.createQuery(criteriaQuery).getResultList();
            transaction.commit();
        } catch(Exception e) {
            if (transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }
        return result;
    }

    public List<User> findUsersByCommunity(Community community) {
        logger.info("Attempting to find users for community {}", community.getId());

        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<User> result = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> userRoot = criteriaQuery.from(User.class);
            criteriaQuery.where(
                    criteriaBuilder.equal(
                            userRoot.get("community"),
                            community
                    )
            );
            result = session.createQuery(criteriaQuery).getResultList();
            transaction.commit();
        } catch(Exception e) {
            if (transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }
        return result;
    }
}
