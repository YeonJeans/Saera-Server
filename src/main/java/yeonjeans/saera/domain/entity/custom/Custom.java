package yeonjeans.saera.domain.entity.custom;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Custom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pitchX;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pitchY;

    @OneToMany(mappedBy = "custom")
    private List<CustomCtag> tags;

    @CreatedDate
    private LocalDateTime createdDate;

    @Builder
    public Custom(String content, String pitchX, String pitchY) {
        this.content = content;
        this.pitchX = pitchX;
        this.pitchY = pitchY;
    }
}
