package yeonjeans.saera.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@NoArgsConstructor
@Getter
@Entity
public class User {

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

    @Column(nullable = false)
    private Date created;

    private Date modified;

    @Builder
    public User(String email, Platform platform, String nickname, String profile, Date created) {
        this.email = email;
        this.platform = platform;
        this.nickname = nickname;
        this.profile = profile;
        this.created = created;
    }

    public void updateXp(int xp){
    }

    public void  updateUser(){
    }
}
