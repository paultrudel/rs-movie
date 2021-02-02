package rsmovie.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import rsmovie.entity.Community;

public interface CommunityRepository extends JpaRepository<Community, Long> { }
