package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import yeonjeans.saera.domain.Login;
import yeonjeans.saera.domain.LoginRepository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.dto.MemberInfoResponseDto;
import yeonjeans.saera.dto.TokenResponseDto;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.exception.ErrorCode;
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

    public void saveRefreshToken(Member member, String refreshToken){
        Optional<Login> result = loginRepository.findByMemberId(member.getId());
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
        loginRepository.save(login);
    }

    public TokenResponseDto reIssueToken(String refreshToken) throws CustomException {
        tokenProvider.validateToken(refreshToken);
        String subject = tokenProvider.getSubject(refreshToken);
        System.out.println(subject);
        log.error(subject);
        Login stored = loginRepository.findByRefreshToken(refreshToken).orElseThrow(()->new CustomException(ErrorCode.WRONG_TOKEN));

        Long memberId = stored.getMember().getId();
        if(subject!=null && memberId != Long.parseLong(subject)) throw new CustomException(ErrorCode.REISSUE_FAILURE);

        String nickname = memberRepository.findById(memberId).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND))
                .getNickname();

        TokenResponseDto dto = tokenProvider.generateToken(memberId, nickname);
        stored.setRefreshToken(dto.getRefreshToken());
        loginRepository.save(stored);

        return dto;
    }

    public MemberInfoResponseDto getMemberInfo(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberInfoResponseDto.builder()
                .name(member.getNickname())
                .email(member.getEmail())
                .profileUrl(member.getProfile())
                .xp(member.getXp())
                .build();
    }
}