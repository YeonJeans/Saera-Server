package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.entity.Practiced;
import yeonjeans.saera.domain.entity.Statement;
import yeonjeans.saera.util.Parsing;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PracticedResponseDto {
    String content;
    LocalDateTime date;
    Double score;
    private List<Integer> pitch_x;
    private List<Double> pitch_y;

    Long id;

    public PracticedResponseDto(Practiced practiced) {
        this.date = practiced.getModifiedDate()!=null? practiced.getModifiedDate() : practiced.getCreatedDate();
        this.score = practiced.getScore();
        this.pitch_x = Parsing.stringToIntegerArray(practiced.getPitchX());
        this.pitch_y = Parsing.stringToDoubleArray(practiced.getPitchY());

        Statement state = practiced.getStatement();
        this.content = state.getContent();
        this.id = state.getId();
    }
}
