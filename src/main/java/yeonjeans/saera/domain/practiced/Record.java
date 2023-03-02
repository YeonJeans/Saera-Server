package yeonjeans.saera.domain.practiced;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import yeonjeans.saera.domain.practiced.Practiced;
import yeonjeans.saera.domain.statement.Statement;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path;

    @Column(columnDefinition =  "LONGBLOB")
    private byte[] wavFile;

    @Builder
    public Record(String path){
        this.path = path;
    }

    public Record(String path, byte[] wavFile) {
        this.path = path;
        this.wavFile = wavFile;
    }
}
