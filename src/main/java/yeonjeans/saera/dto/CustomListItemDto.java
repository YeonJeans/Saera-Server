package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.custom.Custom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CustomListItemDto {
    private String content;
    private List<String> tags;
    private LocalDateTime date;

    private Long id;
    private Boolean practiced;
    private Boolean bookmarked;

    public CustomListItemDto(Object[] result){
        Custom custom = result[0] instanceof Custom ? ((Custom) result[0]) : null;
        Bookmark bookmark = result[1] instanceof Bookmark ? ((Bookmark) result[1]) : null;
        Practice practice = result[2] instanceof Practice ? ((Practice) result[2]) : null;

        this.id = custom.getId();
        this.practiced = practice != null;
        this.bookmarked = bookmark != null;

        this.content = custom.getContent();
        this.tags = custom.getTags().stream()
                .map(customCtag -> customCtag.getTag().getName())
                .collect(Collectors.toList());
        this.date = practiced ? practice.getModifiedDate() : null;
    }
}
