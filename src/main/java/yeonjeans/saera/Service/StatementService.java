package yeonjeans.saera.Service;

import org.springframework.core.io.Resource;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.statement.Tag;

import java.util.List;
import java.util.Optional;

public interface StatementService {

    Optional<Statement> searchById(Long id);
    Tag searchByTag(String tag);
    List<Statement> searchByContent(String content);

    List<Statement> getList();

    Resource getTTS(Long id);
}
