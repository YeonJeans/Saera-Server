package yeonjeans.saera.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.Service.MemberService;
import yeonjeans.saera.security.service.GoogleOAuth;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.domain.member.Platform;
import yeonjeans.saera.dto.oauth.GoogleUser;

import yeonjeans.saera.dto.TokenResponseDto;
import yeonjeans.saera.security.jwt.TokenProvider;
import yeonjeans.saera.security.service.OAuthService;

@Log4j2
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final OAuthService oAuthService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

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
}