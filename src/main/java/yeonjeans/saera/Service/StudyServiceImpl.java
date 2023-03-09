package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.example.StatementRepository;
import yeonjeans.saera.domain.repository.example.WordRepository;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.dto.CompleteStudyRequestDto;
import yeonjeans.saera.dto.ListItemDto;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

import static yeonjeans.saera.util.XPConstant.*;
import static yeonjeans.saera.domain.entity.example.ReferenceType.*;

@RequiredArgsConstructor
@Service
public class StudyServiceImpl {
    private final MemberRepository memberRepository;
    private final StatementRepository statementRepository;
    private final WordRepository wordRepository;

    @Transactional
    public List<Object> completeStudy(CompleteStudyRequestDto requestDto, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if(requestDto.isTodayStudy()){
            int xp = requestDto.getType() == STATEMENT ? XP_TODAY_STATEMENT : XP_TODAY_WORD;
            member.addXp(xp);
            memberRepository.save(member);
        }
        switch (requestDto.getType()){
            case STATEMENT:
                return statementRepository.findAllByIdWithBookmarkAndPractice(member, requestDto.getFkList())
                        .stream()
                        .map(ListItemDto::new)
                        .collect(Collectors.toList());
            case WORD:
                return wordRepository.findAllByIdWithBookmarkAndPractice(member, requestDto.getFkList())
                        .stream()
                        .map(ListItemDto::new)
                        .collect(Collectors.toList());
        }
        return null;
    }
}