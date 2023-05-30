package yeonjeans.saera.security.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeonjeans.saera.dto.oauth.AppleUser;
import yeonjeans.saera.dto.oauth.GoogleUser;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final GoogleOAuth googleOauth;
    private final AppleOAuth appleOAuth;

    public GoogleUser getUserInfo(String code){
        return googleOauth.getUserInfo(code);
    }

    public String getUserInfo(AppleUser dto){
        Claims claims = appleOAuth.getClaims(dto.getToken());
        appleOAuth.verifyIdToken(claims);

        return claims.get("email", String.class);
    }
}
