package yeonjeans.saera.domain.repository.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.domain.entity.member.Member;

import java.util.List;

public interface StatementRepository extends JpaRepository<Statement, Long> {

    List<Statement> findByContentContaining(String content);

    @Query("SELECT s, b, p " +
            "FROM Statement s " +
            "LEFT JOIN Bookmark b ON s.id = b.fk AND b.type = 'STATEMENT' AND b.member = :member" +
            "LEFT JOIN Practice p ON s.id = p.fk AND p.type = 'STATEMENT' AND p.member = :member")
    List<Object[]> getStatementBookmarksAndPractices(@Param("member")Member member);

}
