package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yeonjeans.saera.Service.StatementServiceImpl;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.statement.StatementTag;
import yeonjeans.saera.domain.statement.Tag;
import yeonjeans.saera.dto.StatementResponseDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class StatementController {

    private final StatementServiceImpl statementService;

    @Operation(summary = "문장 세부 조회", description = "id를 이용하여 statement 레코드를 단건 조회합니다.", tags = { "Statement Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = StatementResponseDto.class))),
                    @ApiResponse(responseCode = "204", description = "존재하지 않는 리소스 접근")
            })
    @GetMapping("/statements/{id}")
    public ResponseEntity returnStatement(@PathVariable Long id){
        Optional<Statement> s = statementService.searchById(id);

        if(s.isPresent()){
            StatementResponseDto dto = new StatementResponseDto(s.get());
            return ResponseEntity.ok().body(dto);
        }else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "문장 검색", description = "문장 내용(content)나 tag이름을 이용하여 문장리스트를 검색합니다.", tags = { "Statement Controller" },
            responses = {
                @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = StatementResponseDto.class)))})
            }
    )
    @GetMapping("/statements")
    public ResponseEntity<List<StatementResponseDto>> searchStatement(
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "tags", required= false)ArrayList<String> tags
    ){
        List<StatementResponseDto> list;

        if(content!=null){
            list = statementService.searchByContent(content).stream()
                    .map(StatementResponseDto::new)
                    .collect(Collectors.toList());
        }else if(tags!=null){
            list = tags.stream().map(statementService::searchByTag)
                    .map(Tag::getStatements)
                    .flatMap(Collection::stream)
                    .map(StatementTag::getStatement)
                    .distinct()
                    .map(StatementResponseDto::new)
                    .collect(Collectors.toList());
        }else{
            list = statementService.getList().stream()
                    .map(StatementResponseDto::new)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok().body(list);
    }

}
