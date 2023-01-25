package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.domain.bookmark.Bookmark;
import yeonjeans.saera.domain.bookmark.BookmarkRepository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.statement.StatementRepository;
import yeonjeans.saera.dto.BookmarkResponseDto;
import yeonjeans.saera.dto.StatementResponseDto;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final StatementRepository statementRepository;

    @Operation(summary = "즐겨찾기 문장 조회", description = "즐겨찾기 된 문장 리스트가 제공됩니다.", tags = { "Bookmark Controller" })
    @GetMapping("/statements/bookmark")
    public ResponseEntity returnBookmarkList(){
        Optional<Member> member = memberRepository.findById(1L);
        if(member.isPresent()){
            List<BookmarkResponseDto> list = bookmarkRepository.findAllByMember(member.get())
                    .stream()
                    .map(BookmarkResponseDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(list);
        }else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "즐겨찾기 생성", description = "즐겨찾기 추가", tags = { "Bookmark Controller" })
    @PostMapping("/statements/{id}/bookmark")
    public ResponseEntity createBookmark(@PathVariable Long id){
        Optional<Member> member = memberRepository.findById(1L);
        Optional<Statement> state = statementRepository.findById(id);
        if(member.isPresent()&&state.isPresent()){
            Bookmark bookmark = bookmarkRepository.save(
                    new Bookmark().builder()
                            .statement(state.get())
                            .member(member.get())
                            .build());
            return ResponseEntity.accepted().body(new BookmarkResponseDto(bookmark));
        }else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기 삭제", tags = { "Bookmark Controller" })
    @DeleteMapping("/statements/bookmark/{id}")
    public ResponseEntity deleteBookmark(@PathVariable Long id){
        Optional<Bookmark> bookmark = bookmarkRepository.findById(id);
        if(bookmark.isPresent()){
            bookmarkRepository.delete(bookmark.get());
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().build();
        }

    }
}
