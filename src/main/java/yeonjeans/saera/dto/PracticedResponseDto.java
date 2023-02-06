package yeonjeans.saera.dto;

import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import yeonjeans.saera.domain.practiced.Practiced;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.util.Parsing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PracticedResponseDto {
    String content;
    LocalDateTime date;
    Double score;
    private List<Integer> pitch_x;
    private List<Double> pitch_y;

    Long statement_id;

    public PracticedResponseDto(Practiced practiced) {
        this.date = practiced.getModifiedDate()!=null? practiced.getModifiedDate() : practiced.getCreatedDate();
        this.score = practiced.getScore();
        this.pitch_x = Parsing.stringToIntegerArray(practiced.getPitchX());
        this.pitch_y = Parsing.stringToDoubleArray(practiced.getPitchY());

        Statement state = practiced.getStatement();
        this.content = state.getContent();
        this.statement_id = state.getId();
    }
}
