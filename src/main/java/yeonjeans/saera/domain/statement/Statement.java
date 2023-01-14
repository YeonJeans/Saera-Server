package yeonjeans.saera.domain.statement;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Builder
    public Statement(String content, String record, String record_img) {
        this.content = content;
        this.record = record;
        this.record_img = record_img;
    }

}
