package yeonjeans.saera.Service;

import org.springframework.core.io.Resource;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.StatementResponseDto;

import java.util.ArrayList;
import java.util.List;

public interface StatementService {

    public StatementResponseDto getStatement(Long id, Long memberId);

    public List<StateListItemDto> getStatements(String content, ArrayList<String> tags, Long memberId);

    public List<StateListItemDto> getPracticedStatements(Long memberId);

    public List<StateListItemDto> getBookmarkedStatements(Long memberId);

    public Resource getTTS(Long id);
}
