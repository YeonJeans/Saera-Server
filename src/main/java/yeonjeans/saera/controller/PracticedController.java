package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.Service.StatementServiceImpl;
import yeonjeans.saera.domain.practiced.Practiced;
import yeonjeans.saera.domain.practiced.PracticedRepository;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.dto.PracticedResponseDto;
import yeonjeans.saera.dto.StatementResponseDto;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class PracticedController {

    private final PracticedRepository practicedRepository;

    @Operation(summary = "학습한 문장 조회", description = "학습한 문장 리스트가 제공됩니다.", tags = { "Practiced Controller" })
    @GetMapping("/statements/practiced")
    public ResponseEntity returnPracticedList(){
        List<Practiced> list = practicedRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "학습정보만 조회", description = "간단한 학습정보만 제공됩니다.(학습진행후)", tags = { "Practiced Controller" })
    @GetMapping("/practiced/{id}")
    public ResponseEntity returnPracticed(@PathVariable Long id){
        return null;
    }

    @Operation(summary = "학습 정보 생성", description = "학습 정보 생성", tags = { "Practiced Controller" })
    @PostMapping("/statements/{id}/practiced")
    public ResponseEntity createPracticed(@PathVariable Long id){
        return null;
    }

    @Operation(summary = "학습 정보 업데이트", description = "학습 정보 업데이트(복습)", tags = { "Practiced Controller" })
    @PatchMapping("/statements/{id}/practiced")
    public ResponseEntity updatedPracticed(@PathVariable Long id){
        return null;
    }
}
