package yeonjeans.saera.domain.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.BaseTimeEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>();

    @Column(nullable = false)
    private String nickname;

    private String profile;

    private int xp;

    @Builder
    public Member(String email, Platform platform, String nickname, String profile, HashSet<MemberRole> roleSet) {
        this.email = email;
        this.platform = platform;
        this.nickname = nickname;
        this.profile = profile;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addMemberRole(MemberRole memberRole){
        roleSet.add(memberRole);
    }
}
