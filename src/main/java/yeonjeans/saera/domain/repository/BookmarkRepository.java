package yeonjeans.saera.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.entity.Statement;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    public List<Bookmark> findAllByMember(Member member);
    public Optional<Bookmark> findByStatementAndMember(Statement statement, Member member);
    public boolean existsByStatementAndMember(Statement statement, Member member);
}
