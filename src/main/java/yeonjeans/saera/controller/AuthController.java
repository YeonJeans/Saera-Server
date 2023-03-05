package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yeonjeans.saera.Service.MemberService;
import yeonjeans.saera.Service.MemberServiceImpl;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.domain.member.Platform;
import yeonjeans.saera.dto.MemberInfoResponseDto;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.TokenResponseDto;
import yeonjeans.saera.dto.oauth.GoogleUser;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.exception.ErrorCode;
import yeonjeans.saera.exception.ErrorResponse;
import yeonjeans.saera.security.dto.AuthMember;
import yeonjeans.saera.security.jwt.TokenProvider;
import yeonjeans.saera.security.service.OAuthService;


import javax.validation.Valid;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final OAuthService oAuthService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final MemberServiceImpl memberServiceImpl;

    @Operation(summary = "토큰 발급", description = "구글 Server Auth Code를 통해 유저 정보를 받아오고, 토큰 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = TokenResponseDto.class)))
            }
    )
    @GetMapping(value = "/auth/google/callback")
    public ResponseEntity<?> callback(
            @RequestParam(name = "code") String code) {

        GoogleUser userInfo = oAuthService.getUserInfo(Platform.GOOGLE, code);

        Member member;
        TokenResponseDto dto;
        Boolean isExist = memberRepository.existsByEmailAndPlatform(userInfo.getEmail(), Platform.GOOGLE);

        //login
        if(isExist){
            member = memberRepository.findByEmail(userInfo.getEmail(), Platform.GOOGLE).get();
            dto = memberService.login(member);
        }
        //join
        else{
            member = userInfo.toMember();
            dto = memberService.join(member);
        }
        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "유저 정보 조회", description = "Access Token을 이용하여 유저 정보 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberInfoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "499", description = "토큰 만료로 인한 인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/member")
    public ResponseEntity<?> returnMemberInfo(@RequestHeader String authorization){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        MemberInfoResponseDto dto = memberService.getMemberInfo(principal.getId());
        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 이용하여 AccessToken, RefreshToken을 재발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = TokenResponseDto.class)))}),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "499", description = "토큰 만료로 인한 인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/reissue-token")
    public ResponseEntity<?> reissueToken(@Valid @RequestHeader(required = true) String refreshToken){
        if (refreshToken.length() > 7 && refreshToken.startsWith("Bearer")) {
            TokenResponseDto dto = memberService.reIssueToken(refreshToken.substring(7));
            return ResponseEntity.ok().body(dto);
        }
        throw new CustomException(ErrorCode.BEARER_ERROR);
    }

    @GetMapping("test/accessToken")
    public ResponseEntity<?> testAuthToken(@RequestParam(required = true) String email){
        Member member = memberRepository.findByEmail(email, Platform.GOOGLE).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        String token = tokenProvider.createAccessTokenForTest(member.getId(), member.getNickname());
        return ResponseEntity.ok().body(token);
    }

    @GetMapping("test/refreshToken")
    public ResponseEntity<?> testRefreshToken(@RequestParam(required = true) String email){
        Member member = memberRepository.findByEmail(email, Platform.GOOGLE).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        String token = tokenProvider.createRefreshTokenForTest(member.getId());

        memberServiceImpl.saveRefreshToken(member, token);
        return ResponseEntity.ok().body(token);
    }
}