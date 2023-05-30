package yeonjeans.saera.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.dto.oauth.ApplePublicKeyResponse;
import yeonjeans.saera.exception.CustomException;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

import static yeonjeans.saera.exception.ErrorCode.APPLE_AUTH_ERROR;
import static yeonjeans.saera.exception.ErrorCode.COMMUNICATION_FAILURE;

@Slf4j
@RequiredArgsConstructor
@Component
public class AppleOAuth {
    private final WebClient webClient;

    @Value("${APPLE.AUD}")
    private String aud;
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    public Claims getClaims(String idToken) {
        try {
            String headerOfIdToken = idToken.substring(0, idToken.indexOf("."));
            Map<String, String> header = new ObjectMapper().readValue(
                    new String(Base64.getDecoder().decode(headerOfIdToken), "UTF-8"), Map.class);
            PublicKey publicKey = buildPublicKey(getPublicKey(header.get("kid"), header.get("alg")));

            return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(idToken).getBody();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.info("[in getClaims] public key is not found");
            throw new CustomException(APPLE_AUTH_ERROR);
        } catch (MalformedJwtException e) {
            log.info("[in getClaims] id token is Malformed");
            throw new CustomException(APPLE_AUTH_ERROR);
        } catch (ExpiredJwtException e) {
            log.info("[in getClaims] id token is Expired");
            throw new CustomException(APPLE_AUTH_ERROR);
        } catch (Exception e) {
            log.info("[in getClaims] 알 수 없는 예외");
            throw new CustomException(APPLE_AUTH_ERROR);
        }
    }

    public void verifyIdToken(Claims claims){
        if(claims.getAudience().equals(aud)){
            log.info("[in verifyIdToken] aud is not matched");
            throw new CustomException(APPLE_AUTH_ERROR);
        }
        if(claims.getIssuer().equals("https://appleid.apple.com")){
            log.info("[in verifyIdToken] iss is not matched");
            throw new CustomException(APPLE_AUTH_ERROR);
        }
    }

    private ApplePublicKeyResponse.Key getPublicKey(String kid, String alg) {
        ApplePublicKeyResponse keys = webClient.get()
                .uri("https://appleid.apple.com/auth/keys")
                .retrieve()
                .bodyToMono(ApplePublicKeyResponse.class)
                .block();
        return keys.getMatchedKey(kid, alg)
                .orElseThrow(()->new CustomException(COMMUNICATION_FAILURE));
    }

    private PublicKey buildPublicKey(ApplePublicKeyResponse.Key key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String n = key.getN();
        String kty = key.getKty();
        String e = key.getE();

        byte[] nBytes = Base64.getUrlDecoder().decode(n);
        byte[] eBytes = Base64.getUrlDecoder().decode(e);

        BigInteger nValue = new BigInteger(1, nBytes);
        BigInteger eValue = new BigInteger(1, eBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(nValue, eValue);
        KeyFactory keyFactory = KeyFactory.getInstance(kty);
        return keyFactory.generatePublic(publicKeySpec);
    }
}