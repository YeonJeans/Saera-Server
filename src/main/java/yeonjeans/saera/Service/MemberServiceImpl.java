package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import yeonjeans.saera.domain.Login;
import yeonjeans.saera.domain.LoginRepository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.dto.TokenResponseDto;
import yeonjeans.saera.security.jwt.TokenProvider;

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Repository
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final LoginRepository loginRepository;
    private final TokenProvider tokenProvider;

    @Override
    public TokenResponseDto login(Member request) {
        TokenResponseDto dto = tokenProvider.generateToken(request.getId(), request.getNickname());
        saveRefreshToken(request, dto.getRefreshToken());
        return dto;
    }

    @Override
    public TokenResponseDto join(Member request) {
        Member member = memberRepository.save(request);
        TokenResponseDto dto = tokenProvider.generateToken(member.getId(), member.getNickname());
        saveRefreshToken(request, dto.getRefreshToken());
        return dto;
    }

    private void saveRefreshToken(Member member, String refreshToken){
        Optional<Login> result = loginRepository.findByMember_Id(member.getId());
        Login login;
        if(result.isPresent()){
            login = result.get();
            login.setRefreshToken(refreshToken);
        }
        else{
            login = loginRepository.save(
                    Login.builder().RefreshToken(refreshToken).member(member).build()
            );
        }
    }
}
