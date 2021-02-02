package rsmovie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movie")
@Getter
@Setter
public class Movie {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "score")
    private Double score;

    @OneToMany(cascade = {
            CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE
    }, mappedBy = "movie")
    @JsonIgnore
    private Set<Review> reviews;

    @OneToMany(cascade = {
            CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE
    }, mappedBy = "movie")
    @JsonIgnore
    private Set<Prediction> predictions;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public void addReview(Review review) {
        if(reviews == null) {
            reviews = new HashSet<>();
        }
        reviews.add(review);
        review.setMovie(this);
    }

    public void addPrediction(Prediction prediction) {
        if(predictions == null) {
            predictions = new HashSet<>();
        }
        predictions.add(prediction);
        prediction.setMovie(this);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Movie)) {
            return false;
        }
        Movie movie = (Movie) o;
        return (this.id.equals(movie.getId()));
    }
}
