package yeonjeans.saera.domain.statement;

import lombok.Builder;

import javax.persistence.*;

public class StatementTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY)
    final Statement statement;

    @OneToMany(fetch = FetchType.LAZY)
    final Tag tag;

    @Builder
    public StatementTag(Statement statement, Tag tag) {
        this.statement = statement;
        this.tag = tag;
    }
}
