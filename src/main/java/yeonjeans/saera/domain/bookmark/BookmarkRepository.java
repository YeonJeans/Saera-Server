package yeonjeans.saera.domain.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.member.Member;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    public List<Bookmark> findAllByMember(Member member);
}
