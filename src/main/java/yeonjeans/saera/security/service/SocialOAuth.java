package yeonjeans.saera.domain.member;

import org.springframework.http.ResponseEntity;
import yeonjeans.saera.dto.oauth.GoogleUser;

public interface SocialOAuth {
    /**
     * API Server로부터 받은 code를 활용하여 사용자 인증 정보 요청
     * @param code API Server 에서 받아온 code
     * @return API 서버로 부터 응답 반환
     */
   ResponseEntity<String> requestAccessToken(String code);
    public GoogleUser getUserInfo(String code);
    
}
