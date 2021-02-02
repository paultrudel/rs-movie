package rsmovie.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "username")
    private String username;

    @Column(name = "avg_score")
    private Double avgScore;

    @OneToMany(cascade = {
            CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE
    }, mappedBy = "user")
    @JsonIgnore
    private Set<Review> reviews;

    @OneToMany( cascade = {
            CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE
    }, mappedBy = "user")
    @JsonIgnore
    private Set<Prediction> predictions;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_topic",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @JsonIgnore
    private Set<Topic> topics;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    public void addReview(Review review) {
        if(reviews == null) {
            reviews = new HashSet<>();
        }
        reviews.add(review);
        review.setUser(this);
    }

    public void addPrediction(Prediction prediction) {
        if(predictions == null) {
            predictions = new HashSet<>();
        }
        predictions.add(prediction);
        prediction.setUser(this);
    }

    public void addTopic(Topic topic) {
        if(topics == null) {
            topics = new HashSet<>();
        }
        topics.add(topic);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return (this.id.equals(user.getId()));
    }
}
