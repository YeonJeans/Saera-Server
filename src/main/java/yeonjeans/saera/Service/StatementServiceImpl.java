package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.example.ReferenceType;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.PracticeRepository;
import yeonjeans.saera.domain.repository.example.StatementRepository;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.dto.ListItemDto;
import yeonjeans.saera.dto.NameIdDto;
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
    private final PracticeRepository practiceRepository;
    private final WebClientService webClient;

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

    public List<ListItemDto> getPracticedStatements(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        List<Object[]> list = statementRepository.findPracticed(member, ReferenceType.STATEMENT);
        return list.stream().map(ListItemDto::new).collect(Collectors.toList());
    }

    public List<ListItemDto> getBookmarkedStatements(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        List<Object[]> list = statementRepository.findBookmarked(member, ReferenceType.STATEMENT);
        return list.stream().map(ListItemDto::new).collect(Collectors.toList());
    }

    @Override
    public List<ListItemDto> getStatements(String content, ArrayList<String> tags, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Stream<Object[]> stream;
        if(content==null&&tags==null){
            stream = statementRepository.findAllWithBookmarkAndPractice(member).stream();
        }else if(content!=null&&tags!=null){
            List<Long> idList = statementRepository.findAllByTagnameInAndContentContaining(tags, '%'+content+'%');
            stream = statementRepository.findAllByIdWithBookmarkAndPractice(member, idList).stream();
        }else if(content!=null){
            stream = statementRepository.findAllByContentContaining(member,'%'+content+'%').stream();
        }else{
            List<Long> idList = statementRepository.findAllByTagnameIn(tags);
            stream = statementRepository.findAllByIdWithBookmarkAndPractice(member, idList).stream();
        }

        if(content != null){
            return Stream.concat(stream.map(ListItemDto::new),
                    statementRepository.findAllByIdWithBookmarkAndPractice(member, webClient.getRecommendList(content))
                            .stream().map(item -> new ListItemDto(item, true)))
                    .collect(Collectors.toList());
        }
        return stream.map(ListItemDto::new).collect(Collectors.toList());
    }

    @Override
    public Resource getTTS(Long id) {
        Statement statement = statementRepository.findById(id)
                .orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));

        return new ByteArrayResource(statement.getFile()) {
            @Override
            public String getFilename() {
                return "audio.wav";
            }
        };
    }

    @Override
    public List<NameIdDto> getTop5Statements() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Long> fkList = practiceRepository.findTop5ByCount(ReferenceType.STATEMENT, pageable);

        if(fkList.size() < 5){
            fkList.addAll(practiceRepository.findRestTop5ByCount(ReferenceType.STATEMENT, Pageable.ofSize(5 - fkList.size())));
        }

        List<Statement> statementList = statementRepository.findAllById(fkList);
        return statementList.stream()
                .map(statement ->new NameIdDto(statement.getContent(), statement.getId()))
                .collect(Collectors.toList());
    }

    private Stream<Object[]> searchByTagList(ArrayList<String> tags, Member member) {
        List<Long> idList = statementRepository.findAllByTagnameIn(tags);
        return statementRepository.findAllByIdWithBookmarkAndPractice(member, idList).stream();
    }
}