package rsmovie.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rsmovie.dao.TermRepository;
import rsmovie.entity.Term;
import rsmovie.service.TermService;

import java.util.Optional;

@Service
public class TermServiceImpl implements TermService {

    private TermRepository termRepository;

    public TermServiceImpl(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

    @Override
    public Optional<Term> findById(Long id) {
        return termRepository.findById(id);
    }

    @Override
    public Page<Term> findAll(Pageable pageable) {
        return termRepository.findAll(pageable);
    }

    @Override
    public Page<Term> findByTopicId(Long id, Pageable pageable) {
        return termRepository.findByTopicId(id, pageable);
    }

    @Override
    public Term save(Term term) {
        return termRepository.save(term);
    }
}
