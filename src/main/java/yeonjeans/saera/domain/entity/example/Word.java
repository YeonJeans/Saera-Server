package yeonjeans.saera.domain.entity.example;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(columnDefinition = "LONGBLOB")
    private Byte[] file;

    private String notation;

    private String definition;

    private String pronunciation;

    @OneToOne
    private Tag tag;

    @Builder
    public Word(String content, Byte[] file, String notation, String definition, String pronunciation, Tag tag) {
        this.content = content;
        this.file = file;
        this.notation = notation;
        this.definition = definition;
        this.pronunciation = pronunciation;
        this.tag = tag;
    }
}
