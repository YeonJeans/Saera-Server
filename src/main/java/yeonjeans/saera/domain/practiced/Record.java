package yeonjeans.saera.domain.practiced;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(columnDefinition = "MEDIUMBLOB")
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