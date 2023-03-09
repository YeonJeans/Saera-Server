package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.example.Statement;

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

    public StateListItemDto(Object[] result){
        Statement statement = result[0] instanceof Statement ? ((Statement) result[0]) : null;
        Bookmark bookmark = result[1] instanceof Bookmark ? ((Bookmark) result[1]) : null;
        Practice practice = result[2] instanceof Practice ? ((Practice) result[2]) : null;

        this.id = statement.getId();
        this.practiced = practice != null;
        this.bookmarked = bookmark != null;

        this.content = statement.getContent();
        this.tags = statement.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
        this.date = practiced ? practice.getModifiedDate() : null;
    }
}
