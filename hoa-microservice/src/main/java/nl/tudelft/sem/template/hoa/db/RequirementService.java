package nl.tudelft.sem.template.hoa.db;

import nl.tudelft.sem.template.hoa.domain.Requirement;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.RequirementAlreadyPresent;
import nl.tudelft.sem.template.hoa.exception.RequirementDoesNotExist;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * A DDD service for requirement-related queries.
 */
@Service
public class RequirementService {
    private final transient RequirementRepo requirementRepo;

    /**
     * Constructor for the requirement service.
     *
     * @param requirementRepo the requirement repository
     */
    public RequirementService(RequirementRepo requirementRepo) {
        this.requirementRepo = requirementRepo;
    }

    public List<Requirement> getHOARequirements(long hoaId) throws HoaDoesntExistException {
        Optional<List<Requirement>> optReqs = requirementRepo.findByHoaId(hoaId);
        if (optReqs.isEmpty()) throw new HoaDoesntExistException("Hoa with provided id does not exist");
        return optReqs.get();
    }

    public Requirement addHOARequirement(long hoaId, String prompt) throws HoaDoesntExistException,
            RequirementAlreadyPresent {
        Optional<List<Requirement>> optReqs = requirementRepo.findByHoaId(hoaId);
        if (optReqs.isEmpty()) throw new HoaDoesntExistException("Hoa with provided id does not exist");
        if (optReqs.get().stream().anyMatch(s -> s.getHoaId() == hoaId && s.getPrompt().equals(prompt)))
            throw new RequirementAlreadyPresent("Requirement is already present");
        Requirement newReq = new Requirement(prompt, hoaId);
        newReq = requirementRepo.save(newReq);
        return newReq;
    }

    public Requirement removeHOARequirement(long reqId) throws RequirementDoesNotExist {
        Optional<Requirement> optReq = requirementRepo.findById(reqId);
        if (optReq.isEmpty()) throw new RequirementDoesNotExist("Requirement with provided id does not exist");
        requirementRepo.delete(optReq.get());
        return optReq.get();
    }
}
