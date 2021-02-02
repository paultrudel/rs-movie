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
@Table(name = "community")
@Getter
@Setter
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "community_number")
    private Long communityNumber;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "community")
    @JsonIgnore
    private Set<User> users;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "community_topic",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @JsonIgnore
    private Set<Topic> topics;

    public void addUser(User user) {
        if(users == null) {
            users = new HashSet<>();
        }
        users.add(user);
        user.setCommunity(this);
    }

    public void addTopic(Topic topic) {
        if(topics == null) {
            topics = new HashSet<>();
        }
        topics.add(topic);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Community)) {
            return false;
        }
        Community community = (Community) o;
        return (this.id == community.getId());
    }
}
