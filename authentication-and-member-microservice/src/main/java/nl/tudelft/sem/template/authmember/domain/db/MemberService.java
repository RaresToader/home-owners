package nl.tudelft.sem.template.authmember.domain.db;

import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyExistsException;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import org.springframework.stereotype.Service;

/**
 * A DDD service for member-related queries.
 */
@Service
public class MemberService {
    private final transient MemberRepository memberRepository;

    /**
     * Instantiates a new MemberService.
     *
     */
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Register a new member.
     * @throws Exception if the user already exists
     */
    public Member registerUser(RegistrationModel model) throws MemberAlreadyExistsException{

        Member member = new Member(model.getMemberID(), model.getPassword());

        if (!memberRepository.existsByMemberID(member.getMemberID())) {
            memberRepository.save(member);
            return member;
        }

        throw new MemberAlreadyExistsException(model);
    }

    /**
     * Updates member's password
     * @param model
     * @return Member if password updated successfully
     */
    public Member updatePassword(RegistrationModel model){

        Member member = new Member(model.getMemberID(), model.getPassword());

        if (memberRepository.existsByMemberID(member.getMemberID())) {
            memberRepository.save(member);
            return member;
        }

        throw new IllegalArgumentException(model.getMemberID());
    }

    public Member getMember(String memberID){
        if (memberRepository.existsByMemberID(memberID)) {
            return memberRepository.findByMemberID(memberID).get();
        }
        throw new IllegalArgumentException(memberID);
    }
}