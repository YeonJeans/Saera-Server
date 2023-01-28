package yeonjeans.saera.dto;

import lombok.Data;
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
    String graph_x;
    String graph_y;
    String record;

    Long statement_id;
    Long practiced_id;

    public PracticedResponseDto(Practiced practiced) {
        this.practiced_id = practiced.getId();

        this.date = practiced.getModifiedDate()!=null? practiced.getModifiedDate() : practiced.getCreatedDate();
        this.accuracy = practiced.getAccuracy();
        this.graph_x = practiced.getGraphX();
        this.graph_y = practiced.getGraphY();
        this.record = practiced.getRecord();

        Statement state = practiced.getStatement();
        this.content = state.getContent();
        this.statement_id = state.getId();
    }
}
