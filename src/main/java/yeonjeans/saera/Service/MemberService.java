package yeonjeans.saera.Service;

import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.dto.TokenResponseDto;

public interface MemberService {

    @Transactional
    TokenResponseDto join(Member request);

    TokenResponseDto login(Member request);

    @Transactional
    public JSONObject reIssueToken(String refreshToken);
}
