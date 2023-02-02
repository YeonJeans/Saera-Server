package yeonjeans.saera.dto;

import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import yeonjeans.saera.domain.practiced.Practiced;
import yeonjeans.saera.domain.statement.Statement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PracticedResponseDto {
    String content;
    LocalDateTime date;
    Integer accuracy;
    String pitch_x;
    String pitch_y;

    Long statement_id;
    Long practiced;

    public PracticedResponseDto(Practiced practiced) {
        this.practiced= practiced.getId();

        this.date = practiced.getModifiedDate()!=null? practiced.getModifiedDate() : practiced.getCreatedDate();
        this.accuracy = practiced.getAccuracy();
        this.pitch_x = practiced.getGraphX();
        this.pitch_y = practiced.getGraphY();

        Statement state = practiced.getStatement();
        this.content = state.getContent();
        this.statement_id = state.getId();
    }
}
