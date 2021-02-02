package rsmovie.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "topic")
@Getter
@Setter
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "topic")
    @JsonIgnore
    private Set<Term> terms;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "topic")
    @JsonIgnore
    private Set<Movie> movies;

    @ManyToMany(mappedBy = "topics", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<User> users;

    @ManyToMany(mappedBy = "topics", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Community> communities;

    public void addTerm(Term term) {
        if(terms == null) {
            terms = new HashSet<>();
        }
        terms.add(term);
        term.setTopic(this);
    }

    public void addMovie(Movie movie) {
        if(movies == null) {
            movies = new HashSet<>();
        }
        movies.add(movie);
        movie.setTopic(this);
    }

    public void addUser(User user) {
        if(user == null) {
            users = new HashSet<>();
        }
        users.add(user);
    }

    public void addCommunity(Community community) {
        if(communities == null) {
            communities = new HashSet<>();
        }
        communities.add(community);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Topic)) {
            return false;
        }
        Topic topic = (Topic) o;
        return (this.id == topic.getId());
    }
}
