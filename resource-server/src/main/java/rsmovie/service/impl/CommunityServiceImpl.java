package rsmovie.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rsmovie.dao.CommunityRepository;
import rsmovie.entity.Community;
import rsmovie.entity.Topic;
import rsmovie.entity.User;
import rsmovie.service.CommunityService;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityServiceImpl implements CommunityService {

    private CommunityRepository communityRepository;

    public CommunityServiceImpl(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Override
    public Optional<Community> findById(Long id) {
        return communityRepository.findById(id);
    }

    @Override
    public Page<Community> findAll(Pageable pageable) {
        return communityRepository.findAll(pageable);
    }

    @Override
    public Community save(Community community) {
        return communityRepository.save(community);
    }
}
