package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import yeonjeans.saera.domain.entity.member.Login;
import yeonjeans.saera.domain.repository.member.LoginRepository;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.member.MemberRepository;
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
        TokenResponseDto dto = tokenProvider.generateToken(request.getId(), request.getName());
        saveRefreshToken(request, dto.getRefreshToken());
        return dto;
    }

    @Override
    public TokenResponseDto join(Member request) {
        Member member = memberRepository.save(request);
        TokenResponseDto dto = tokenProvider.generateToken(member.getId(), member.getName());
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
                    Login.builder().refreshToken(refreshToken).member(member).build()
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
                .getName();

        TokenResponseDto dto = tokenProvider.generateToken(memberId, nickname);
        stored.setRefreshToken(dto.getRefreshToken());
        loginRepository.save(stored);

        return dto;
    }

    public MemberInfoResponseDto getMemberInfo(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberInfoResponseDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .profileUrl(member.getProfileUrl())
                .xp(member.getXp())
                .build();
    }

    @Override
    public MemberInfoResponseDto updateMember(Long id, String name) {
        Member member = memberRepository.findById(id).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.setNickname(name);
        return new MemberInfoResponseDto(memberRepository.save(member));
    }
}