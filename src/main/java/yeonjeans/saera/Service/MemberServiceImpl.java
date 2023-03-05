package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
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
    }

    public JSONObject reIssueToken(String refreshToken) throws CustomException {
        Login stored = loginRepository.findByRefreshToken(refreshToken).orElseThrow(()->new CustomException(ErrorCode.REISSUE_FAILURE));

        if(!refreshToken.equals(stored.getRefreshToken()))
            throw new CustomException(ErrorCode.WRONG_TOKEN);

        Long memberId = stored.getMember().getId();
        String nickname = memberRepository.findById(memberId).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND))
                .getNickname();

        TokenResponseDto dto = tokenProvider.generateToken(memberId, nickname);
        stored.setRefreshToken(dto.getRefreshToken());
        loginRepository.save(stored);
        JSONObject reissued = new JSONObject();
        reissued.put("grantType", dto.getGrantType());
        reissued.put("accessToken", dto.getAccessToken());
        reissued.put("refreshToken", dto.getRefreshToken());

        return reissued;
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
