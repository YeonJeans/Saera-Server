package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.bookmark.Bookmark;
import yeonjeans.saera.domain.bookmark.BookmarkRepository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.statement.StatementRepository;
import yeonjeans.saera.dto.BookmarkResponseDto;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.exception.CustomException;

import static yeonjeans.saera.exception.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookmarkServiceImpl {
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final StatementRepository statementRepository;

    public List<StateListItemDto> getList(Long id){
        Member member = memberRepository.findById(id).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        return bookmarkRepository.findAllByMember(member)
                .stream()
                .map(StateListItemDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookmarkResponseDto create(Long id, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        Statement state = statementRepository.findById(id).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));

        if(bookmarkRepository.existsByStatementAndMember(state, member)){
            throw new CustomException(ALREADY_BOOKMARKED);
        }

        Bookmark bookmark = bookmarkRepository.save(Bookmark.builder()
                        .statement(state)
                        .member(member)
                        .build());

        return new BookmarkResponseDto(bookmark);
    }

    @Transactional
    public void delete(Long id, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        Statement state = statementRepository.findById(id).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByStatementAndMember(state, member).orElseThrow(()->new CustomException(BOOKMARK_NOT_FOUND));
        bookmarkRepository.delete(bookmark);
    }
}
