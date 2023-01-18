package yeonjeans.saera.domain.statement;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "statement")
    private List<StatementTag> statementTag = new ArrayList<>();

    public Tag(String name) {
        this.name = name;
    }
}
