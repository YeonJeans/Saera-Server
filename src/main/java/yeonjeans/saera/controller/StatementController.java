package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yeonjeans.saera.Service.StatementServiceImpl;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.dto.StatementResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class StatementController {

    private final StatementServiceImpl statementService;

    @Operation(summary = "문장 세부 조회", description = "하나의 문장 제공", tags = { "Statement Controller" })
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

    @Operation(summary = "문장 검색", description = "tag, content", tags = { "Statement Controller" })
    @GetMapping("/statements")
    public ResponseEntity<List<StatementResponseDto>> searchStatement(
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "tag", required = false) String tag){

        System.out.println("Content: "+content+"/tag: "+tag);
        List<StatementResponseDto> list;

        if(content!=null){
            list = statementService.searchByContent(content).stream()
                    .map(StatementResponseDto::new)
                    .collect(Collectors.toList());
        }else if(tag!=null){
            list = statementService.searchByTag(tag).stream()
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
