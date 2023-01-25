package yeonjeans.saera.domain.statement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatementRepository extends JpaRepository<Statement, Long> {

    List<Statement> findByContentContaining(String content);

    @Query("SELECT DISTINCT s FROM Statement s JOIN s.tags t WHERE t.id =?1")
    public List<Statement> findByTag(Long tid);

}
