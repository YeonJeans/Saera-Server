package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.Service.MemberService;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.entity.member.Platform;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.dto.TokenResponseDto;
import yeonjeans.saera.dto.oauth.AppleUser;
import yeonjeans.saera.dto.oauth.GoogleUser;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.exception.ErrorCode;
import yeonjeans.saera.exception.ErrorResponse;
import yeonjeans.saera.security.service.OAuthService;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final OAuthService oAuthService;

    @Operation(summary = "토큰 발급", description = "구글 Server Auth Code를 통해 유저 정보를 받아오고, 토큰 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = TokenResponseDto.class)))
            }
    )
    @GetMapping(value = "/auth/google/callback")
    public ResponseEntity<?> callback(
            @RequestParam(name = "code") String code) {

        GoogleUser userInfo = oAuthService.getUserInfo(code);
        Member member;
        TokenResponseDto dto;
        Boolean isExist = memberRepository.existsByEmailAndPlatform(userInfo.getEmail(), Platform.GOOGLE);

        //login
        if(isExist){
            member = memberRepository.findByEmailAndPlatform(userInfo.getEmail(), Platform.GOOGLE).get();
            dto = memberService.login(member);
        }
        //join
        else{
            member = userInfo.toMember();
            dto = memberService.join(member);
        }
        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "애플로그인 후 토큰 발급",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "애플의 id token 검증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @PostMapping(value = "/auth/apple/callback")
    public ResponseEntity<?> callbackApple(
            @RequestBody AppleUser requestDto) {

        String email = oAuthService.getUserInfo(requestDto);

        Member member;
        TokenResponseDto dto;
        Boolean isExist = memberRepository.existsByEmailAndPlatform(requestDto.getIdentifier(), Platform.APPLE);

        //login
        if(isExist){
            member = memberRepository.findByEmailAndPlatform(requestDto.getIdentifier(), Platform.APPLE).get();
            dto = memberService.login(member);
        }
        //join
        else{
            if(requestDto.getName()==null)
                requestDto.setName(email.substring(0, 7));

            member = requestDto.toMember();
            dto = memberService.join(member);
        }
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
    public ResponseEntity<?> reissueToken(@RequestHeader(required = true) String refreshToken){
        if (refreshToken.length() > 7 && refreshToken.startsWith("Bearer")) {
            TokenResponseDto dto = memberService.reIssueToken(refreshToken.substring(7));
            return ResponseEntity.ok().body(dto);
        }
        throw new CustomException(ErrorCode.BEARER_ERROR);
    }
}