package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.Practiced;
import yeonjeans.saera.domain.entity.Statement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Data
public class StateListItemDto {
    private String content;
    private List<String> tags;
    private LocalDateTime date;

    private Long id;
    private Boolean practiced;
    private Boolean bookmarked;


    public StateListItemDto(Practiced practiced, Long memberId) {
        this.practiced = true;

        this.date = practiced.getModifiedDate()!=null? practiced.getModifiedDate() : practiced.getCreatedDate();

        Statement state = practiced.getStatement();
        this.content = state.getContent();
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
        this.id = state.getId();
        this.bookmarked = state.getBookmarks().stream().anyMatch(bookmark -> bookmark.getMember().getId().equals(memberId));
    }

    public StateListItemDto(Bookmark bookmark, Long memberId) {
        Statement state = bookmark.getStatement();

        this.content = state.getContent();
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
        this.id = state.getId();
        this.bookmarked = true;
        try{
            Practiced practiced = state.getPracticeds().stream()
                    .filter(practice -> practice.getMember().getId().equals(memberId))
                    .findFirst().orElseThrow();
            this.practiced = true;
            this.date = practiced.getModifiedDate()!=null? practiced.getModifiedDate() : practiced.getCreatedDate();
        }catch (NoSuchElementException exception){
            this.date = null;
            this.practiced = false;
        }
    }

    public StateListItemDto(Statement state, Long memberId){
        this.content = state.getContent();
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
        this.id = state.getId();
        this.bookmarked = state.getBookmarks().stream().anyMatch(i->i.getMember().getId().equals(memberId));
        try{
            Practiced practiced = state.getPracticeds().stream()
                    .filter(practice -> practice.getMember().getId().equals(memberId))
                    .findFirst().orElseThrow();
            this.practiced = true;
            this.date = practiced.getModifiedDate()!=null? practiced.getModifiedDate() : practiced.getCreatedDate();
        }catch (NoSuchElementException exception){
            this.date = null;
            this.practiced = false;
        }
    }
}
