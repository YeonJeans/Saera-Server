package yeonjeans.saera.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeonjeans.saera.domain.member.GoogleOAuth;
import yeonjeans.saera.domain.member.Platform;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final GoogleOAuth googleOauth;
    private final HttpServletResponse response;

    public void request(Platform platform) throws IOException {
        String redirectURL;
        switch (platform){
            case GOOGLE:{
                //각 소셜 로그인을 요청하면 소셜로그인 페이지로 리다이렉트 해주는 프로세스이다.
                redirectURL= googleOauth.getOauthRedirectURL();
            }break;
            default:{
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }

        }

        response.sendRedirect(redirectURL);
    }

    public String requestAccessToken(Platform platform, String code){
        switch (platform){
            case GOOGLE:{
                return googleOauth.requestAccessToken(code);
            }
            case APPLE:{

            }
            default: {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 타입입니다.");
            }
        }
    }
}
