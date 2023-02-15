package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.dto.LoginRequestDto;
import yeonjeans.saera.exception.CustomException;

import static yeonjeans.saera.exception.ErrorCode.*;

@RequiredArgsConstructor
@Repository
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public String createToken(LoginRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        //비밀번호 확인 등의 유효성 검사 진행
        return null;
    }

}
