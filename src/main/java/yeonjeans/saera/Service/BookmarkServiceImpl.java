package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.repository.BookmarkRepository;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.MemberRepository;
import yeonjeans.saera.domain.entity.Statement;
import yeonjeans.saera.domain.repository.StatementRepository;
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
                .map(bookmark -> new StateListItemDto(bookmark, member.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean create(Long id, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        Statement state = statementRepository.findById(id).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));

        if(bookmarkRepository.existsByStatementAndMember(state, member)){
            throw new CustomException(ALREADY_BOOKMARKED);
        }

        Bookmark bookmark = bookmarkRepository.save(Bookmark.builder()
                        .statement(state)
                        .member(member)
                        .build());

        return bookmark != null;
    }

    @Transactional
    public void delete(Long id, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        Statement state = statementRepository.findById(id).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByStatementAndMember(state, member).orElseThrow(()->new CustomException(BOOKMARK_NOT_FOUND));
        bookmarkRepository.delete(bookmark);
    }
}
