package yeonjeans.saera.domain.statement;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String record;

    @Column(nullable = false)
    private String record_img;

    @OneToMany(mappedBy = "tag")
    private List<StatementTag> statementTag;

    @Builder
    public Statement(String content, String record, String record_img) {
        this.content = content;
        this.record = record;
        this.record_img = record_img;
    }
}
