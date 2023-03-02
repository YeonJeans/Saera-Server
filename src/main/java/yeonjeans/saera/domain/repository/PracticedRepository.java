package yeonjeans.saera.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.entity.Practiced;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.entity.Statement;

import java.util.List;
import java.util.Optional;

public interface PracticedRepository extends JpaRepository<Practiced, Long> {
    Optional<Practiced> findById(Long id);
    Optional<Practiced> findByStatementAndMember(Statement statement, Member member);
    List<Practiced> findAllByMemberOrderByCreatedDateDesc(Member member);
    Boolean existsByStatementAndMember(Statement statement, Member member);
}
