package yeonjeans.saera.Service;

import yeonjeans.saera.domain.statement.Statement;

import java.util.List;
import java.util.Optional;

public interface StatementService {

    Optional<Statement> searchById(Long id);
    List<Statement> searchByTag(String tag);
    List<Statement> searchByContent(String content);

    List<Statement> getList();
}
