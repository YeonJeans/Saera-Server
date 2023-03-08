package yeonjeans.saera.domain.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.example.ReferenceType;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.member.MemberRepository;

import java.util.List;

@SpringBootTest
public class PracticeRepositoryTest {
    @Autowired
    PracticeRepository practiceRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void get(){
        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).get();

        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, ReferenceType.STATEMENT, 9L).get();

        Assertions.assertNotNull(practice);
    }
}
