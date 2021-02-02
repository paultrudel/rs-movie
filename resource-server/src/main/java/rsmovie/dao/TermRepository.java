package rsmovie.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rsmovie.entity.Term;

public interface TermRepository extends JpaRepository<Term, Long> {

    Page<Term> findByTopicId(Long id, Pageable pageable);
}
