package yeonjeans.saera.Service;

import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.dto.TokenResponseDto;

public interface MemberService {

    @Transactional
    TokenResponseDto join(Member request);

    TokenResponseDto login(Member request);
}
