package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.example.ReferenceType;
import yeonjeans.saera.domain.entity.member.Search;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.domain.repository.member.SearchRepository;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.domain.repository.example.StatementRepository;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.StatementResponseDto;
import yeonjeans.saera.exception.CustomException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static yeonjeans.saera.exception.ErrorCode.MEMBER_NOT_FOUND;
import static yeonjeans.saera.exception.ErrorCode.STATEMENT_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class StatementServiceImpl implements StatementService {
    private final StatementRepository statementRepository;
    private final MemberRepository memberRepository;
    private final SearchRepository searchRepository;
    private final WebClient webClient;

    private final String MLserverBaseUrl;
    @Value("${ml.secret}")
    private String ML_SECRET;
    @Value("${clova.client-id}")
    private String CLOVA_ID;
    @Value("${clova.client-secret}")
    private String CLOVA_SECRET;

    @Override
    public StatementResponseDto getStatement(Long id, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        List<Object[]> list = statementRepository.findByIdWithBookmarkAndPractice(member, id);
        if(list.isEmpty()) throw new CustomException(STATEMENT_NOT_FOUND);
        Object[] result = list.get(0);

        Statement statement = result[0] instanceof Statement ? ((Statement) result[0]) : null;
        Bookmark bookmark = result[1] instanceof Bookmark ? ((Bookmark) result[1]) : null;
        Practice practice = result[2] instanceof Practice ? ((Practice) result[2]) : null;

        return new StatementResponseDto(statement, bookmark, practice);
    }

    public List<StateListItemDto> getPracticedStatements(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        List<Object[]> list = statementRepository.findPracticed(member, ReferenceType.STATEMENT);
        return list.stream().map(StateListItemDto::new).collect(Collectors.toList());
    }

    public List<StateListItemDto> getBookmarkedStatements(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        List<Object[]> list = statementRepository.findBookmarked(member, ReferenceType.STATEMENT);
        return list.stream().map(StateListItemDto::new).collect(Collectors.toList());
    }

    @Override
    public List<StateListItemDto> getStatements(String content, ArrayList<String> tags, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Stream<Object[]> stream;
        if(content==null&&tags==null){
            return statementRepository.findAllWithBookmarkAndPractice(member).stream().map(StateListItemDto::new).collect(Collectors.toList());
        }else if(content!=null&&tags!=null){
            stream = Stream.concat(statementRepository.findAllByContentContaining(member,'%'+content+'%').stream(), searchByTagList(tags, member))
                    .distinct();
        }else if(content!=null){
            stream = statementRepository.findAllByContentContaining(member,'%'+content+'%').stream();
        }else{
            stream = searchByTagList(tags, member);
        }
        return stream.map(i->addSearchHistory(i, member)).collect(Collectors.toList());
    }

    @Override
    public List<StateListItemDto> getSearchHistory(Long memberId, int pageSize) {
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "modifiedDate"));
        Page<Search> page = searchRepository.findAllByMemberOrderByCreatedDateDesc(member, pageable);

        List<Long> idList = page.get().map(i->i.getStatement().getId()).collect(Collectors.toList());

        return statementRepository.findAllByIdWithBookmarkAndPractice(member, idList).stream()
                .map(StateListItemDto::new).collect(Collectors.toList());
    }

    @Override
    public Resource getTTS(Long id) {
        Statement statement = statementRepository.findById(id)
                .orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));
        String content = statement.getContent();

        return webClient.post()
                .uri("https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts")
                .headers(headers -> {
                    headers.set("Content-Type", "application/x-www-form-urlencoded");
                    headers.set("X-NCP-APIGW-API-KEY-ID", CLOVA_ID);
                    headers.set("X-NCP-APIGW-API-KEY", "2Y0KJ5v5U2eSajYUN7aiJwMXr5E8LuniKct7Vt1S");
                })
                .body(BodyInserters.fromFormData
                                ("speaker", "vhyeri")
                        .with("text", content)
                        .with("format", "wav"))
                .retrieve()
                .bodyToMono(Resource.class)
                .block();
    }

    private Stream<Object[]> searchByTagList(ArrayList<String> tags, Member member) {
        List<Long> idList = statementRepository.findAllByTagnameIn(tags);
        return statementRepository.findAllByIdWithBookmarkAndPractice(member, idList).stream();
    }

    @Transactional
    StateListItemDto addSearchHistory(Object[] objects, Member member){
        searchRepository.save(new Search(member,(Statement) objects[0]));
        return new StateListItemDto(objects);
    }
}
