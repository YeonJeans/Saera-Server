package yeonjeans.saera.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.entity.example.ReferenceType;
import yeonjeans.saera.domain.entity.member.Member;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Practice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "LONGBLOB")
    private Byte[] file;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pitchX;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pitchY;

    private Double score;

    private Integer Count;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private ReferenceType type;

    private Long fk;

    @Builder
    public Practice(Member member, Byte[] file, String pitchX, String pitchY, Double score, ReferenceType type, Long fk) {
        this.member = member;
        this.file = file;
        this.pitchX = pitchX;
        this.pitchY = pitchY;
        this.score = score;
        this.type = type;
        this.fk = fk;
    }

    public void setCount(Integer count) {
        Count = count;
    }

    public void setFile(Byte[] file) {
        this.file = file;
    }

    public void setPitchX(String pitchX) {
        this.pitchX = pitchX;
    }

    public void setPitchY(String pitchY) {
        this.pitchY = pitchY;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
