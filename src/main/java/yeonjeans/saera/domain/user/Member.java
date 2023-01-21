package yeonjeans.saera.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.BaseTimeEntity;

import javax.persistence.*;

@NoArgsConstructor
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

    @Column(nullable = false)
    private String nickname;

    private String profile;

    private int xp;

    @Builder
    public Member(String email, Platform platform, String nickname, String profile) {
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
}
