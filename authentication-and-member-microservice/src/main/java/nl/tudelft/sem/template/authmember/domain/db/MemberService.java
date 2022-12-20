package nl.tudelft.sem.template.authmember.domain.db;

import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadRegistrationModelException;
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
    public Member registerUser(RegistrationModel model) throws MemberAlreadyExistsException, BadRegistrationModelException {
        if (!validateUsername(model.getMemberId()) || !validatePassword(model.getPassword())) {
            throw new BadRegistrationModelException("Bad username or password!");
        }
        Member member = new Member(model.getMemberId(), passwordHashingService.hash(model.getPassword()));
        if (!memberRepository.existsByMemberId(member.getMemberId())) {
            memberRepository.save(member);
            return member;
        }

        throw new MemberAlreadyExistsException(model);
    }

    /**
     * Validates a username. It is valid if it is non-empty, non-null and non-blank.
     * It is valid if without leading/trailing spaces it has at least than 6 characters and
     * at most 20. Any character is allowed.
     *
     * @param name the name
     * @return true if the name passes the format, false otherwise
     */
    public boolean validateUsername(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.length() >= 6 && trimmed.length() <= 20;
    }

    /**
     * Method that validates a password. It needs to be non-null, non-empty, non-blank.
     * It also must have between 10 and 20 characters. Any character is allowed.
     *
     * @param password the password
     * @return true if the password matches the format, false otherwise
     */
    public boolean validatePassword(String password) {
        if (password == null || password.isBlank() || password.isEmpty()) {
            return false;
        }
        return password.length() >= 10 && password.length() <= 32;
    }

    /**
     * Updates member's password.
     *
     * @param model the registration model
     * @return Member if password updated successfully
     */
    public Member updatePassword(RegistrationModel model) throws BadRegistrationModelException {
        if (!validatePassword(model.getPassword())) {
            throw new BadRegistrationModelException("Bad username or password!");
        }
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