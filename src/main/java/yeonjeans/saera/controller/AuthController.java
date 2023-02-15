package yeonjeans.saera.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.Service.MemberServiceImpl;
import yeonjeans.saera.domain.member.Platform;
import yeonjeans.saera.dto.LoginRequestDto;
import yeonjeans.saera.dto.TokenResponseDto;
import yeonjeans.saera.security.service.OAuthService;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final MemberServiceImpl memberService;
    private final OAuthService oAuthService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        String token = memberService.createToken(loginRequest);
        return ResponseEntity.ok().body(new TokenResponseDto(token, "bearer"));
    }

    @GetMapping(value = "/auth/google/callback")
    public String callback(
            @RequestParam(name = "code") String code) {
        log.info(">> 소셜 로그인 API 서버로부터 받은 code :: {}", code);
        return oAuthService.requestAccessToken(Platform.GOOGLE, code);
    }

    @GetMapping("/auth/{socialLoginType}")
    public void socialLoginRedirect(@PathVariable String socialLoginType) throws IOException {
        Platform platform = Platform.GOOGLE;
        oAuthService.request(platform);
    }
}
