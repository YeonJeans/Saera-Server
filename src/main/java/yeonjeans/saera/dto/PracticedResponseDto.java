package yeonjeans.saera.dto;

import yeonjeans.saera.domain.statement.Statement;

import java.util.List;
import java.util.stream.Collectors;

public class PracticedResponseDto {
    private String content;
    private String record;
    private String graphX;
    private String graphY;

    List<String> tags;

    public PracticedResponseDto(Statement state) {
        this.content = state.getContent();
        this.record = state.getRecord();
        this.graphX = state.getGraphX();
        this.graphY = state.getGraphY();
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
    }
}
