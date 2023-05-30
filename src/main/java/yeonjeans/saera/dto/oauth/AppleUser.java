package yeonjeans.saera.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.entity.member.MemberRole;
import yeonjeans.saera.domain.entity.member.Platform;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppleUser {
    String code;
    String token;
    String state;

    @NotNull
    String identifier;
    String email;
    String name;

    public Member toMember(){
        Member member = Member.builder()
                .platform(Platform.APPLE)
                .email(identifier)
                .name(name)
                .build();
        member.addMemberRole(MemberRole.USER);
        return member;
    }

    public void setName(String name) {
        this.name = name;
    }
}
