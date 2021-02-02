package rsmovie.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rsmovie.entity.Topic;

import java.util.Optional;

public interface TopicService {

    Optional<Topic> findById(Long id);
    Page<Topic> findAll(Pageable pageable);
    Topic save(Topic topic);
}
