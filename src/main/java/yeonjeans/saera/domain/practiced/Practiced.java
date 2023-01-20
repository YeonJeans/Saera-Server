package yeonjeans.saera.domain.practiced;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.BaseTimeEntity;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.user.User;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Practiced extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Statement statement;

    private String record;

    private String record_img;

    private Integer accuracy;

    private Boolean is_practiced;

    @Builder
    public Practiced(User user, Statement statement) {
        this.user = user;
        this.statement = statement;
    }

    @Builder
    public Practiced(User user, Statement statement, String record, String record_img, Integer accuracy, Boolean is_practiced) {
        this.user = user;
        this.statement = statement;
        this.record = record;
        this.record_img = record_img;
        this.accuracy = accuracy;
        this.is_practiced = is_practiced;
    }
}
