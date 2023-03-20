package yeonjeans.saera.domain.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @Test
    public void get(){
        //when
        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).get();

        Practice practice1 = Practice.builder().type(ReferenceType.WORD).fk(1L).member(member).build();
        practiceRepository.save(practice1);

        //given
        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, ReferenceType.STATEMENT, 1L).get();

        //then
        Assertions.assertNotNull(practice);
    }

    @Test
    public void getTop5() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Long> list = practiceRepository.findTop5ByCount(ReferenceType.STATEMENT, pageable);
        System.out.println(list.size());

        for(Long id: list){
            System.out.println(id);
        }

        List<Long> list2 = practiceRepository.findRestTop5ByCount(ReferenceType.STATEMENT, pageable);
        System.out.println(list2.size());

        for(Long id: list2){
            System.out.println(id);
        }
    }
}
