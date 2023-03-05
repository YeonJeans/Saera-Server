package yeonjeans.saera.domain.entity.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import yeonjeans.saera.domain.entity.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Login extends BaseTimeEntity {

    @Id
    @Column
    private String RefreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    public void setRefreshToken(String refreshToken) {
        RefreshToken = refreshToken;
    }
}