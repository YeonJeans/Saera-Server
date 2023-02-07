package yeonjeans.saera.domain.practiced;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.statement.Statement;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface PracticedRepository extends JpaRepository<Practiced, Long> {
    Optional<Practiced> findById(Long id);
    Optional<Practiced> findByStatementAndMember(Statement statement, Member member);
    List<Practiced> findAllByMemberOrderByCreatedDateDesc(Member member);
    Boolean existsByStatementAndMember(Statement statement, Member member);
}
