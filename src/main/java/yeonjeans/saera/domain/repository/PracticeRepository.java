package yeonjeans.saera.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.example.ReferenceType;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.entity.example.Statement;

import java.util.List;
import java.util.Optional;

public interface PracticeRepository extends JpaRepository<Practice, Long> {
    public List<Practice> findAllByMemberAndTypeOrderByCreatedDateDesc(Member member, ReferenceType type);

    public Optional<Practice> findByMemberAndTypeAndFk(Member member, ReferenceType type, Long fk);

    public Boolean existsByMemberAndTypeAndFk(Member member, ReferenceType type, Long fk);
}
