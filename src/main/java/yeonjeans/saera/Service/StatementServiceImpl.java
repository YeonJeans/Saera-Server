package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.domain.statement.*;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.exception.CustomException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static yeonjeans.saera.exception.ErrorCode.STATEMENT_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class StatementServiceImpl implements StatementService {
    private final StatementRepository statementRepository;
    private final TagRepository tagRepository;
    private final WebClient webClient;

    @Override
    public Statement searchById(Long id) {
        return statementRepository.findById(id).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));
    }

    public List<StateListItemDto> search(String content, ArrayList<String> tags){
        if(content==null&&tags==null){
            return statementRepository.findAll()
                    .stream().map(StateListItemDto::new).collect(Collectors.toList());
        }else if(content!=null&&tags!=null){
            return Stream.concat(statementRepository.findByContentContaining(content).stream(), searchByTagList(tags))
                    .distinct()
                    .map(StateListItemDto::new)
                    .collect(Collectors.toList());

        }else if(content!=null){
            return statementRepository.findByContentContaining(content).stream().map(StateListItemDto::new).collect(Collectors.toList());
        }else{
            return searchByTagList(tags).map(StateListItemDto::new).collect(Collectors.toList());
        }

    }

    public Stream<Statement> searchByTagList(ArrayList<String> tags) {
        return tags.stream().map(tagRepository::findByName)
                .map(Tag::getStatements)
            .flatMap(Collection::stream)
            .map(StatementTag::getStatement)
            .distinct();
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
