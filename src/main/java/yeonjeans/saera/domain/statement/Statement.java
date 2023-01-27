package yeonjeans.saera.domain.statement;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.bookmark.Bookmark;
import yeonjeans.saera.domain.practiced.Practiced;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String graphX;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String graphY;

    @OneToMany(mappedBy = "statement")
    private List<StatementTag> tags;

    @OneToMany(mappedBy = "statement")
    private List<Bookmark> bookmarks;

    @OneToMany(mappedBy = "statement")
    private List<Practiced> practiceds;

    @Builder
    public Statement(String content, String record, String graphX, String graphY) {
        this.content = content;
        this.record = record;
        this.graphX = graphX;
        this.graphY = graphY;
    }

}
