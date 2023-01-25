package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.statement.StatementRepository;
import yeonjeans.saera.domain.statement.Tag;
import yeonjeans.saera.domain.statement.TagRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StatementServiceImpl implements StatementService {
    private final StatementRepository statementRepository;
    private final TagRepository tagRepository;

    @Override
    public Optional<Statement> searchById(Long id) {
        return statementRepository.findById(id);
    }

    @Override
    public List<Statement> searchByContent(String content) {
        return statementRepository.findByContentContaining(content);
    }

    @Override
    public List<Statement> searchByTag(String keyword) {
        Tag tag = tagRepository.findByName(keyword);
        if(tag!=null){
            return statementRepository.findByTag(tag.getId());
        }else{
            return statementRepository.findByTag(0L);
        }

    }

    @Override
    public List<Statement> getList() {
        return statementRepository.findAll();
    }
}
