package yeonjeans.saera.Service;

import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.Query;
import yeonjeans.saera.domain.entity.example.ReferenceType;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.StatementResponseDto;
import yeonjeans.saera.exception.CustomException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static yeonjeans.saera.exception.ErrorCode.MEMBER_NOT_FOUND;

public interface StatementService {

    public StatementResponseDto getStatement(Long id, Long memberId);

    public List<StateListItemDto> getStatements(String content, ArrayList<String> tags, Long memberId);

    public List<StateListItemDto> getPracticedStatements(Long memberId);

    public List<StateListItemDto> getBookmarkedStatements(Long memberId);

    public List<StateListItemDto> getSearchHistory(Long memberId, int pageSize);

    public Resource getTTS(Long id);
}
