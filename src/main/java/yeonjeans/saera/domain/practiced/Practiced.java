package yeonjeans.saera.domain.practiced;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.BaseTimeEntity;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.member.Member;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Practiced extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Statement statement;

    private String record;

    private String record_img;

    private Integer accuracy;

    private Boolean is_practiced;

    @Builder
    public Practiced(Member member, Statement statement) {
        this.member =member;
        this.statement = statement;
    }

    @Builder
    public Practiced(Member member, Statement statement, String record, String record_img, Integer accuracy, Boolean is_practiced) {
        this.member = member;
        this.statement = statement;
        this.record = record;
        this.record_img = record_img;
        this.accuracy = accuracy;
        this.is_practiced = is_practiced;
    }
}
