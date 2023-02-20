package yeonjeans.saera.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {

    public Optional<Login> findByMember_Id(Long MemberId);
}
