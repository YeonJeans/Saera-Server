package yeonjeans.saera.domain.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.statement.Statement;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    public List<Bookmark> findAllByMember(Member member);
    public Optional<Bookmark> findByStatementAndMember(Statement statement, Member member);
}
