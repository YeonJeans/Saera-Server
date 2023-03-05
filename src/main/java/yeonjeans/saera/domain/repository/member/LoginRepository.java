package yeonjeans.saera.domain.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeonjeans.saera.domain.entity.member.Login;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {

    public Optional<Login> findByMemberId(Long MemberId);

//    @Query("select l from Login l where l.RefreshToken = :token")
//    public Optional<Login> findByRefreshToken(@Param("token")String refreshToken);

    public Optional<Login> findByRefreshToken(String refreshToken);
}