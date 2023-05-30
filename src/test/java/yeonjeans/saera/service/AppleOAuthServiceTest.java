package yeonjeans.saera.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yeonjeans.saera.security.service.AppleOAuth;

@SpringBootTest
@RequiredArgsConstructor
public class AppleOAuthServiceTest {

    @Autowired
    private AppleOAuth appleOAuth;

    @Test
    public void verifyIdToken(){
        String idToken = "";
        Claims claims = appleOAuth.getClaims(idToken);
        appleOAuth.verifyIdToken(claims);

        System.out.println(claims.getAudience());
        System.out.println(claims.getIssuer());
        System.out.println(claims.get("email", String.class));
    }
}
