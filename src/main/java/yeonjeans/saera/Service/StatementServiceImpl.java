package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.statement.StatementRepository;
import yeonjeans.saera.domain.statement.Tag;
import yeonjeans.saera.domain.statement.TagRepository;
import yeonjeans.saera.exception.CustomException;

import java.util.List;
import java.util.Optional;

import static yeonjeans.saera.exception.ErrorCode.STATEMENT_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class StatementServiceImpl implements StatementService {
    private final StatementRepository statementRepository;
    private final TagRepository tagRepository;
    private final WebClient webClient;

    @Override
    public Optional<Statement> searchById(Long id) {
        return statementRepository.findById(id);
    }

    @Override
    public List<Statement> searchByContent(String content) {
        return statementRepository.findByContentContaining(content);
    }

    @Override
    public Tag searchByTag(String tag) {
        return tagRepository.findByName(tag);
    }

    @Override
    public List<Statement> getList() {
        return statementRepository.findAll();
    }

    @Override
    public Resource getTTS(Long id) {
        Statement statement = statementRepository.findById(id)
                .orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));
        String content = statement.getContent();

        return webClient.get()
                .uri("/tts?text="+content)
                .retrieve()
                .bodyToMono(Resource.class)
                .block();
    }
}
