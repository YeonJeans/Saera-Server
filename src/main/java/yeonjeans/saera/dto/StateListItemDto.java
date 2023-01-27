package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.bookmark.Bookmark;
import yeonjeans.saera.domain.practiced.Practiced;
import yeonjeans.saera.domain.statement.Statement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Data
public class StateListItemDto {
    private String content;
    private List<String> tags;
    private LocalDateTime date;

    private Long statement_id;
    private Long practiced_id;
    private Long bookmark_id;


    public StateListItemDto(Practiced practiced) {
        this.practiced_id = practiced.getId();

        this.date = practiced.getModifiedDate()!=null? practiced.getModifiedDate() : practiced.getCreatedDate();

        Statement state = practiced.getStatement();
        this.content = state.getContent();
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
        this.statement_id = state.getId();
        try{
            this.bookmark_id = state.getBookmarks().stream()
                    .filter(bookmark -> bookmark.getMember().getId().equals(1L))
                    .findFirst().orElseThrow().getId();
        }catch (NoSuchElementException exception){
            this.bookmark_id = null;
        }
    }

    public StateListItemDto(Bookmark bookmark) {
        Statement state = bookmark.getStatement();

        this.content = state.getContent();
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
        this.statement_id = state.getId();
        this.bookmark_id = bookmark.getId();
        try{
            Practiced practiced = state.getPracticeds().stream()
                    .filter(practice -> practice.getMember().getId().equals(1L))
                    .findFirst().orElseThrow();
            this.practiced_id = practiced.getId();
            this.date = practiced.getModifiedDate()!=null? practiced.getModifiedDate() : practiced.getCreatedDate();
        }catch (NoSuchElementException exception){
            this.date = null;
            this.practiced_id = null;
        }

    }
}
