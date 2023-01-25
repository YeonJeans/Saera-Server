package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.statement.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class StatementResponseDto {
    private String content;
    private String record;
    private String graphX;
    private String graphY;
    private boolean isPracticed;

    List<String> tags;

    public StatementResponseDto(Statement state) {
        this.content = state.getContent();
        this.record = state.getRecord();
        this.graphX = state.getGraphX();
        this.graphY = state.getGraphY();
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
    }

}
