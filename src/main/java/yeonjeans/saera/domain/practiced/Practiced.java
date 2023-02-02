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
    private String graphX;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String graphY;

    private Integer accuracy;

    @Builder
    public Practiced(Member member, Statement statement) {
        this.member = member;
        this.statement = statement;
    }

    @Builder
    public Practiced(Member member, Statement statement, Record record, String graphX, String graphY, Integer accuracy) {
        this.member = member;
        this.statement = statement;
        this.record = record;
        this.graphX = graphX;
        this.graphY = graphY;
        this.accuracy = accuracy;
    }
}
