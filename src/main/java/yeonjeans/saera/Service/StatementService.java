package yeonjeans.saera.Service;

import org.springframework.core.io.Resource;
import yeonjeans.saera.dto.NameIdDto;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.StatementResponseDto;

import java.util.ArrayList;
import java.util.List;

public interface StatementService {

    StatementResponseDto getStatement(Long id, Long memberId);

    List<StateListItemDto> getStatements(String content, ArrayList<String> tags, Long memberId);

    List<StateListItemDto> getPracticedStatements(Long memberId);

    List<StateListItemDto> getBookmarkedStatements(Long memberId);

    Resource getTTS(Long id);

    List<NameIdDto> getTop5Statements();
}
