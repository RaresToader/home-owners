package nl.tudelft.sem.template.authmember.domain.db;

import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyExistsException;
import nl.tudelft.sem.template.authmember.domain.password.PasswordHashingService;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import org.springframework.stereotype.Service;

/**
 * A DDD service for member-related queries.
 */
@Service
public class MemberService {
    private final transient MemberRepository memberRepository;
    private final transient PasswordHashingService passwordHashingService;

    /**
     * Instantiates a new MemberService.
     */
    public MemberService(MemberRepository memberRepository, PasswordHashingService passwordHashingService) {
        this.memberRepository = memberRepository;
        this.passwordHashingService = passwordHashingService;
    }

    /**
     * Register a new member.
     *
     * @throws MemberAlreadyExistsException if the user already exists
     */
    public Member registerUser(RegistrationModel model) throws MemberAlreadyExistsException {

        Member member = new Member(model.getMemberId(), passwordHashingService.hash(model.getPassword()));

        if (!memberRepository.existsByMemberId(member.getMemberId())) {
            memberRepository.save(member);
            return member;
        }

        throw new MemberAlreadyExistsException(model);
    }

    /**
     * Updates member's password.
     *
     * @param model the registration model
     * @return Member if password updated successfully
     */
    public Member updatePassword(RegistrationModel model) {

        Member member = new Member(model.getMemberId(), passwordHashingService.hash(model.getPassword()));

        if (memberRepository.existsByMemberId(member.getMemberId())) {
            memberRepository.save(member);
            return member;
        }

        throw new IllegalArgumentException(model.getMemberId());
    }

    /**
     * Return a member by memberId.
     *
     * @param memberId the memberId
     * @return the member
     * @throws IllegalArgumentException if the member does not exist
     */
    public Member getMember(String memberId) {
        if (memberRepository.existsByMemberId(memberId)) {
            if (memberRepository.findByMemberId(memberId).isPresent()) {
                return memberRepository.findByMemberId(memberId).get();
            }
        }
        throw new IllegalArgumentException(memberId);
    }
}