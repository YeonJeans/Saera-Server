package yeonjeans.saera.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.entity.member.Search;
import yeonjeans.saera.domain.entity.member.Member;

import java.util.List;

public interface SearchRepository extends JpaRepository<Search, Long> {
    public List<Search> findTop3ByMemberOrderByCreatedDateDesc(Member member);
}
