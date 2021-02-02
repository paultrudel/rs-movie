package rsmovie.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rsmovie.entity.Term;

import java.util.Optional;

public interface TermService {

    Optional<Term> findById(Long id);
    Page<Term> findAll(Pageable pageable);
    Page<Term> findByTopicId(Long id, Pageable pageable);
    Term save(Term term);
}
