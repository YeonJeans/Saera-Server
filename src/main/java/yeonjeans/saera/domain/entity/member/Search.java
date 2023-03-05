package yeonjeans.saera.domain.entity.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.entity.BaseTimeEntity;
import yeonjeans.saera.domain.entity.example.Statement;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Search extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Statement statement;

    @Builder
    public Search(Member member, Statement statement) {
        this.member = member;
        this.statement = statement;
    }
}
