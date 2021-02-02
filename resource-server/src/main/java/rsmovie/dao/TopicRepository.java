package rsmovie.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import rsmovie.entity.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> { }
