package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.StatementResponseDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final StatementRepository statementRepository;

    @Operation(summary = "즐겨찾기 문장 조회", description = "즐겨찾기 된 문장 리스트가 제공됩니다.", tags = { "Bookmark Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = StatementResponseDto.class)))}),
                    @ApiResponse(responseCode = "204", description = "존재하지 않는 리소스 접근")
            }
    )
    @GetMapping("/statements/bookmark")
    public ResponseEntity<?> returnBookmarkList(){
        Optional<Member> member = memberRepository.findById(1L);
        if(member.isPresent()){
            List<StateListItemDto> list = bookmarkRepository.findAllByMember(member.get())
                    .stream()
                    .map(StateListItemDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(list);
        }else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "즐겨찾기 생성", description = "statement_id를 사용하여 bookmark를 등록합니다.", tags = { "Bookmark Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = BookmarkResponseDto.class))),
            }
    )
    @PostMapping("/statements/{id}/bookmark")
    public ResponseEntity<?> createBookmark(@PathVariable Long id){
        Optional<Member> member = memberRepository.findById(1L);
        Optional<Statement> state = statementRepository.findById(id);
        if(member.isPresent()&&state.isPresent()){
            Bookmark bookmark = bookmarkRepository.save(
                    new Bookmark().builder()
                            .statement(state.get())
                            .member(member.get())
                            .build());
            return ResponseEntity.ok().body(new BookmarkResponseDto(bookmark));
        }else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "즐겨찾기 삭제", description = "bookmark_id를 사용하여 Bookmark를 삭제합니다.", tags = { "Bookmark Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
            }
    )
    @DeleteMapping("/statements/bookmark/{id}")
    public ResponseEntity<?> deleteBookmark(@PathVariable Long id){
        Optional<Bookmark> bookmark = bookmarkRepository.findById(id);
        if(bookmark.isPresent()){
            bookmarkRepository.delete(bookmark.get());
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.internalServerError().build();
        }

    }
}