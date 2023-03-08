package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.example.Word;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.example.WordRepository;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.dto.WordResponseDto;
import yeonjeans.saera.exception.CustomException;

import java.util.List;

import static yeonjeans.saera.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class WordServiceImpl {
    private final MemberRepository memberRepository;
    private final WordRepository wordRepository;

    public WordResponseDto getWord(Long id, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        List<Object[]> list = wordRepository.findByIdWithBookmarkAndPractice(member, id);
        if(list.isEmpty()) throw new CustomException(WORD_NOT_FOUND);
        Object[] result = list.get(0);

        Word word = result[0] instanceof Word ? ((Word) result[0]) : null;
        Bookmark bookmark = result[1] instanceof Bookmark ? ((Bookmark) result[1]) : null;
        Practice practice = result[2] instanceof Practice ? ((Practice) result[2]) : null;

        return new WordResponseDto(word, bookmark, practice);
    }
}
