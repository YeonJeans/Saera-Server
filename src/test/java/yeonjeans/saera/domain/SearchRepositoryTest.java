package yeonjeans.saera.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.domain.member.Platform;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.statement.StatementRepository;

import java.util.List;

@SpringBootTest
public class SearchRepositoryTest {

    @Autowired
    SearchRepository searchRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StatementRepository statementRepository;

    @Transactional
    @Test
    public void searchRecent3(){
        //given
        Member member1 = memberRepository.save(Member.builder().email("test1@gmail.com").nickname("testuser1").platform(Platform.GOOGLE).profile("testProfile").build());
        Member member2 =  memberRepository.save(Member.builder().email("test2@gmail.com").nickname("testuser2").platform(Platform.GOOGLE).profile("testProfile").build());

        Statement statement1 = statementRepository.save(Statement.builder().pitchX("").pitchY("").content("test1").build());
        Statement statement2 = statementRepository.save(Statement.builder().pitchX("").pitchY("").content("test2").build());
        Statement statement3 = statementRepository.save(Statement.builder().pitchX("").pitchY("").content("test3").build());
        Statement statement4 = statementRepository.save(Statement.builder().pitchX("").pitchY("").content("test4").build());

        searchRepository.save(Search.builder().member(member1).statement(statement1).build());
        searchRepository.save(Search.builder().member(member2).statement(statement4).build());
        searchRepository.save(Search.builder().member(member1).statement(statement2).build());
        searchRepository.save(Search.builder().member(member1).statement(statement3).build());
        searchRepository.save(Search.builder().member(member1).statement(statement4).build());


        //when
        List<Search> list = searchRepository.findTop3ByMemberOrderByCreatedDateDesc(member1);
        //then
        for(Search item: list){
            System.out.println(item.getStatement().getContent());
        }
        Assertions.assertEquals("test4", list.get(0).getStatement().getContent());
    }
}
