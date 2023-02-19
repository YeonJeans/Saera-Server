package yeonjeans.saera.domain.member;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Transactional
    @Test
    public void findMemberWithPlatformTest(){
        Member member = Member.builder()
                .profile("test")
                .platform(Platform.GOOGLE)
                .email("test")
                .nickname("testuser1")
                .build();
        member.addMemberRole(MemberRole.USER);
        memberRepository.save(member);

        Member result = memberRepository.findByEmail("test", Platform.GOOGLE).get();

        Assertions.assertEquals(member, result);
    }
}
