package yeonjeans.saera.domain.statement;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@Entity
public class StatementTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Statement> statement;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Tag> tag;

}
