package yeonjeans.saera.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeonjeans.saera.domain.entity.member.Platform;
import yeonjeans.saera.dto.oauth.AppleUser;
import yeonjeans.saera.dto.oauth.GoogleUser;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final GoogleOAuth googleOauth;

    public GoogleUser getUserInfo(String code){
        return googleOauth.getUserInfo(code);
    }

    public Boolean verifyAppleUserInfo(AppleUser dto){
        return true;
    }
}
