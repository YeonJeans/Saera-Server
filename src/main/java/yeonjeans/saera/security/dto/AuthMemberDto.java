package yeonjeans.saera.security.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import yeonjeans.saera.domain.member.Platform;

import java.util.Collection;
import java.util.Map;

@Getter
public class AuthMemberDto extends User implements OAuth2User {
    private String email;
    private String name;
    private Map<String, Object> attr;

    public AuthMemberDto(String username, String password, Collection<? extends GrantedAuthority> authorities){
        super(username, password, authorities);
        this.email = username;
    }

    @Override
    public Map<String, Object> getAttributes(){
        return this.attr;
    }
}
