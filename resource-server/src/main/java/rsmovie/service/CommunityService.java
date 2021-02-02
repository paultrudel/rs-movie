package rsmovie.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rsmovie.entity.Community;

import java.util.List;
import java.util.Optional;

public interface CommunityService {

    Optional<Community> findById(Long id);
    Page<Community> findAll(Pageable pageable);
    Community save(Community community);
}
