package yeonjeans.saera.domain.repository.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeonjeans.saera.domain.entity.example.Word;
import yeonjeans.saera.domain.entity.member.Member;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    @Query("SELECT w, b, p " +
            "FROM Word w " +
            "LEFT JOIN Bookmark b ON w.id = b.fk AND b.type = 1 AND b.member = :member " +
            "LEFT JOIN Practice p ON w.id = p.fk AND p.type = 1 AND p.member = :member " +
            "WHERE w.id = :wordId")
    List<Object[]> findByIdWithBookmarkAndPractice(@Param("member") Member member, @Param("wordId")Long wordId);
}