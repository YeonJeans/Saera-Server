package yeonjeans.saera.domain.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("select m from Member m where  m.email = :email and m.platform = :platform")
    Optional<Member> findByEmail(@Param("email") String email,@Param("platform") Platform platform);

    Boolean existsByEmailAndPlatform(String email, Platform platform);

    @EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Member> findById(Long id);
}
