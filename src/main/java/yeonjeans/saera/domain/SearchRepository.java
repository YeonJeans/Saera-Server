package yeonjeans.saera.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.statement.Statement;

import java.util.List;

public interface SearchRepository extends JpaRepository<Search, Long> {
    public List<Search> findTop3ByMemberOrderByCreatedDateDesc(Member member);
}
