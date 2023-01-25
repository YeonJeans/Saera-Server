package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yeonjeans.saera.domain.member.MemberRepository;

@RequiredArgsConstructor
@Repository
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

}
