package yeonjeans.saera.Service;

import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.dto.MemberInfoResponseDto;
import yeonjeans.saera.dto.TokenResponseDto;
import yeonjeans.saera.exception.CustomException;

public interface MemberService {

    @Transactional
    TokenResponseDto join(Member request);

    TokenResponseDto login(Member request);

    @Transactional
    public TokenResponseDto reIssueToken(String refreshToken);

    public MemberInfoResponseDto getMemberInfo(Long memberId);
}
