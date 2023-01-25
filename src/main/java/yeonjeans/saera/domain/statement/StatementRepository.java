package yeonjeans.saera.domain.statement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatementRepository extends JpaRepository<Statement, Long> {

    List<Statement> findByContentContaining(String content);
}
