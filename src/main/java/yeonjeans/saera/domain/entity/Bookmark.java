package yeonjeans.saera.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.entity.member.Member;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Bookmark extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Statement statement;

    @Builder
    public Bookmark(Member member, Statement statement) {
        this.member = member;
        this.statement = statement;
    }
}
