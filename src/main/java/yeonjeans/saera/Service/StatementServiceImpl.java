package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.domain.Search;
import yeonjeans.saera.domain.SearchRepository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.domain.statement.*;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.exception.CustomException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static yeonjeans.saera.exception.ErrorCode.MEMBER_NOT_FOUND;
import static yeonjeans.saera.exception.ErrorCode.STATEMENT_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class StatementServiceImpl implements StatementService {
    private final StatementRepository statementRepository;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;
    private final SearchRepository searchRepository;
    private final WebClient webClient;

    @Override
    public Statement searchById(Long id) {
        return statementRepository.findById(id).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));
    }

    @Override
    public List<StateListItemDto> search(String content, ArrayList<String> tags, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Stream<Statement> statementStream;
        if(content==null&&tags==null){
            statementStream = statementRepository.findAll().stream();
        }else if(content!=null&&tags!=null){
            statementStream =Stream.concat(statementRepository.findByContentContaining(content).stream(), searchByTagList(tags))
                    .distinct();

        }else if(content!=null){
            statementStream = statementRepository.findByContentContaining(content).stream();
        }else{
            statementStream =  searchByTagList(tags);
        }
        return statementStream.map(i->addSearchHistory(i,member)).collect(Collectors.toList());

    }

    @Transactional
    StateListItemDto addSearchHistory(Statement statement, Member member){
        searchRepository.save(new Search(member, statement));
        return new StateListItemDto(statement);
    }

    @Override
    public List<StateListItemDto> searchHistory(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        List<Search> list = searchRepository.findTop3ByMemberOrderByCreatedDateDesc(member);
        return list.stream().map(item->new StateListItemDto(item.getStatement())).collect(Collectors.toList());
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

    public Stream<Statement> searchByTagList(ArrayList<String> tags) {
        return tags.stream().map(tagRepository::findByName)
                .map(Tag::getStatements)
                .flatMap(Collection::stream)
                .map(StatementTag::getStatement)
                .distinct();
    }
}
