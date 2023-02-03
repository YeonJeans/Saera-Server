package yeonjeans.saera.domain.practiced;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.BaseTimeEntity;
import yeonjeans.saera.domain.Record;
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

    @OneToOne
    private Record record;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pitchX;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pitchY;

    private Integer accuracy;

    @Builder
    public Practiced(Member member, Statement statement) {
        this.member = member;
        this.statement = statement;
    }

    @Builder
    public Practiced(Member member, Statement statement, Record record, String pitchX, String pitchY, Integer accuracy) {
        this.member = member;
        this.statement = statement;
        this.record = record;
        this.pitchX = pitchX;
        this.pitchY = pitchY;
        this.accuracy = accuracy;
    }
}
