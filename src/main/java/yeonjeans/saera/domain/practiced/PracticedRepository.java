package yeonjeans.saera.domain.practiced;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.member.Member;

import java.util.List;

public interface PracticedRepository extends JpaRepository<Practiced, Long> {
    List<Practiced> findAllByMember(Member member);
}
