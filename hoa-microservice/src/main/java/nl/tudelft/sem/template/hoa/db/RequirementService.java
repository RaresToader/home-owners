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

    /**
     * Gets HOA requirements
     *
     * @param hoaId id of HOA
     * @return Fetched requirement, if id exists
     * @throws HoaDoesntExistException Thrown if HOA with provided id does not exist in the database
     */
    public List<Requirement> getHoaRequirements(long hoaId) throws HoaDoesntExistException {
        Optional<List<Requirement>> optReqs = requirementRepo.findByHoaId(hoaId);
        if (optReqs.isEmpty()) throw new HoaDoesntExistException("Hoa with provided id does not exist");
        return optReqs.get();
    }

    /**
     * Gets an HOA requirement
     *
     * @param reqId id of requirement
     * @return Fetched requirement, if id exists
     * @throws RequirementDoesNotExist Thrown if HOA with provided id does not exist in the database
     */
    public Requirement getHoaRequirement(long reqId) throws RequirementDoesNotExist {
        Optional<Requirement> optReq = requirementRepo.findById(reqId);
        if (optReq.isEmpty()) throw new RequirementDoesNotExist("Requirement with provided id does not exist");
        return optReq.get();
    }

    /**
     * Adds an HOA requirement
     *
     * @param hoaId  id of HOA
     * @param prompt String of added requirement's prompt
     * @return Added Requirement entry
     * @throws HoaDoesntExistException   Thrown if HOA with provided id does not exist in the database
     * @throws RequirementAlreadyPresent Thrown if a requirement with the provided prompt exists in the HOA
     */
    public Requirement addHoaRequirement(long hoaId, String prompt) throws HoaDoesntExistException,
            RequirementAlreadyPresent {
        Optional<List<Requirement>> optReqs = requirementRepo.findByHoaId(hoaId);
        if (optReqs.isEmpty()) throw new HoaDoesntExistException("Hoa with provided id does not exist");
        if (optReqs.get().stream().anyMatch(s -> s.getHoaId() == hoaId && s.getPrompt().equals(prompt)))
            throw new RequirementAlreadyPresent("Requirement is already present");
        Requirement newReq = new Requirement(prompt, hoaId);
        newReq = requirementRepo.save(newReq);
        return newReq;
    }

    /**
     * Removes an HOA requirement
     *
     * @param reqId ID of requirement to remove
     * @return Removed Requirement entry, if it exists
     * @throws RequirementDoesNotExist Thrown if a requirement with the provided id does not exist in the database
     */
    public Requirement removeHoaRequirement(long reqId) throws RequirementDoesNotExist {
        Optional<Requirement> optReq = requirementRepo.findById(reqId);
        if (optReq.isEmpty()) throw new RequirementDoesNotExist("Requirement with provided id does not exist");
        //requirementRepo.delete(optReq.get());
        return optReq.get();
    }
}
