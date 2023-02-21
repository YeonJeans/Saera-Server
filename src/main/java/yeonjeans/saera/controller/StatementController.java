package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yeonjeans.saera.Service.StatementService;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.StatementResponseDto;
import yeonjeans.saera.security.dto.AuthMember;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StatementController {

    private final StatementService statementService;

    @Operation(summary = "문장 세부 조회", description = "statement_id를 이용하여 statement 레코드를 단건 조회합니다.", tags = { "Statement Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = StatementResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근")
            })
    @GetMapping("/statements/{id}")
    public ResponseEntity<StatementResponseDto> returnStatement(@PathVariable Long id, @AuthenticationPrincipal AuthMember principal){
        Statement statement = statementService.searchById(id);

        return ResponseEntity.ok().body(new StatementResponseDto(statement, principal.getId()));
    }

    @Operation(summary = "예시 음성 조회", description = "statement id를 이용하여 예시 음성을 조회 합니다.", tags = { "Statement Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = StatementResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근")
            })
    @GetMapping("/statements/record/{id}")
    public ResponseEntity<?> getExampleRecord(@PathVariable Long id){
        Resource resource = statementService.getTTS(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("audio", "wav"));

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @Operation(summary = "문장 검색", description = "문장 내용(content)나 tag이름을 이용하여 문장리스트를 검색합니다.", tags = { "Statement Controller" },
            responses = {
                @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = StatementResponseDto.class)))})
            }
    )
    @GetMapping("/statements")
    public ResponseEntity<?> searchStatement(
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "tags", required= false) ArrayList<String> tags,
            @AuthenticationPrincipal AuthMember principal
    ){
        List<StateListItemDto> list = statementService.search(content, tags, principal.getId());

        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "최근 검색 내역 조회", description = "최근 검색한 문장 3개 제공", tags = { "Statement Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = StatementResponseDto.class)))})
            }
    )
    @GetMapping("/search")
    public ResponseEntity<?> searchHistory(@AuthenticationPrincipal AuthMember principal){
        List<StateListItemDto> list = statementService.searchHistory(principal.getId());
        return ResponseEntity.ok().body(list);
    }
}
