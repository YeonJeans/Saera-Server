package yeonjeans.saera.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.entity.StatementTag;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "tag")
    private List<StatementTag> statements;

    public Tag(String name) {
        this.name = name;
    }
}
