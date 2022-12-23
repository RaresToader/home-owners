package nl.tudelft.sem.template.hoa.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.hoa.domain.Requirement;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class RequirementServiceTest {

    private transient RequirementRepo requirementRepo;
    private transient RequirementService requirementService;
    private transient long hoaId = 1L;

    @BeforeEach
    void setUp() {
        requirementRepo = Mockito.mock(RequirementRepo.class);
        requirementService = new RequirementService(requirementRepo);
    }

    @Test
    void getHoaRequirements() throws HoaDoesntExistException {
        Requirement req1 = new Requirement("Prompt 1", hoaId);
        Requirement req2 = new Requirement("Prompt 2", hoaId);
        List<Requirement> expectedRequirements = Arrays.asList(req1, req2);
        when(requirementRepo.findByHoaId(hoaId)).thenReturn(Optional.of(expectedRequirements));
        List<Requirement> actualRequirements = requirementService.getHoaRequirements(hoaId);
        assertEquals(expectedRequirements, actualRequirements);
    }

    @Test
    void getHoaRequirementsHoaDoesNotExist() {
        when(requirementRepo.findByHoaId(hoaId)).thenReturn(Optional.empty());
        assertThrows(HoaDoesntExistException.class, () -> requirementService.getHoaRequirements(hoaId));
    }
}