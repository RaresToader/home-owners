package nl.tudelft.sem.template.hoa.db;

import org.springframework.stereotype.Service;

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
}
