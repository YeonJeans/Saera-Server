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


    public StateListItemDto(Practice practice, Long memberId) {
//        this.practiced = true;
//
//        this.date = practice.getModifiedDate()!=null? practice.getModifiedDate() : practice.getCreatedDate();
//
//        Statement state = practice.getStatement();
//        this.content = state.getContent();
//        this.tags = state.getTags().stream()
//                .map(statementTag -> statementTag.getTag().getName())
//                .collect(Collectors.toList());
//        this.id = state.getId();
//        this.bookmarked = state.getBookmarks().stream().anyMatch(bookmark -> bookmark.getMember().getId().equals(memberId));
    }

    public StateListItemDto(Bookmark bookmark, Long memberId) {
//        Statement state = bookmark.getStatement();
//
//        this.content = state.getContent();
//        this.tags = state.getTags().stream()
//                .map(statementTag -> statementTag.getTag().getName())
//                .collect(Collectors.toList());
//        this.id = state.getId();
//        this.bookmarked = true;
//        try{
//            Practice practice = state.getPracticeds().stream()
//                    .filter(practice -> practice.getMember().getId().equals(memberId))
//                    .findFirst().orElseThrow();
//            this.practiced = true;
//            this.date = practice.getModifiedDate()!=null? practice.getModifiedDate() : practice.getCreatedDate();
//        }catch (NoSuchElementException exception){
//            this.date = null;
//            this.practiced = false;
//        }
    }

    public StateListItemDto(Statement state, Long memberId){
//        this.content = state.getContent();
//        this.tags = state.getTags().stream()
//                .map(statementTag -> statementTag.getTag().getName())
//                .collect(Collectors.toList());
//        this.id = state.getId();
//        this.bookmarked = state.getBookmarks().stream().anyMatch(i->i.getMember().getId().equals(memberId));
//        try{
//            Practice practice = state.getPracticeds().stream()
//                    .filter(practice -> practice.getMember().getId().equals(memberId))
//                    .findFirst().orElseThrow();
//            this.practiced = true;
//            this.date = practice.getModifiedDate()!=null? practice.getModifiedDate() : practice.getCreatedDate();
//        }catch (NoSuchElementException exception){
//            this.date = null;
//            this.practiced = false;
//        }
    }
}
