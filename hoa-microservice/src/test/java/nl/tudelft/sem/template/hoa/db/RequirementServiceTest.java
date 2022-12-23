package nl.tudelft.sem.template.hoa.db;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.domain.Requirement;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.RequirementAlreadyPresent;
import nl.tudelft.sem.template.hoa.exception.RequirementDoesNotExist;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.INTEGRATION;

@TestSuite(testType = INTEGRATION)
public class RequirementServiceTest {

    private transient RequirementRepo requirementRepo;

    private transient RequirementService requirementService;

    void setUp() {
        requirementService = new RequirementService(requirementRepo);
    }

    @Test
    void emptyHoaRequirements() {
        requirementRepo = Mockito.mock(RequirementRepo.class);
        Mockito.when(requirementRepo.findByHoaId(1L)).thenReturn(Optional.empty());
        setUp();
        Assertions.assertThrows(HoaDoesntExistException.class, () -> requirementService.getHoaRequirements(1L));
    }

    @Test
    void someHoaRequirements() throws HoaDoesntExistException {
        requirementRepo = Mockito.mock(RequirementRepo.class);
        Mockito.when(requirementRepo.findByHoaId(1L)).thenReturn(Optional.of(new ArrayList<>()));
        setUp();
        Assertions.assertEquals(new ArrayList<>(), requirementService.getHoaRequirements(1L));
    }

    @Test
    void addHoaReqNotFound() {
        requirementRepo = Mockito.mock(RequirementRepo.class);
        Mockito.when(requirementRepo.findByHoaId(1L)).thenReturn(Optional.empty());
        setUp();
        Assertions.assertThrows(HoaDoesntExistException.class, () -> requirementService.addHoaRequirement(1L, "Prompt"));
    }

    @Test
    void addHoaReqAlreadyThere() {
        requirementRepo = Mockito.mock(RequirementRepo.class);
        List<Requirement> list = new ArrayList<>();
        Requirement requirement = new Requirement("Prompt", 1L);
        list.add(requirement);
        Mockito.when(requirementRepo.findByHoaId(1L)).thenReturn(Optional.of(list));
        setUp();
        Assertions.assertThrows(RequirementAlreadyPresent.class, () -> requirementService.addHoaRequirement(1L, "Prompt"));
    }

    @Test
    void addHoaReqHappy() throws RequirementAlreadyPresent, HoaDoesntExistException {
        requirementRepo = Mockito.mock(RequirementRepo.class);
        List<Requirement> list = new ArrayList<>();
        Requirement requirement = new Requirement("Prompt", 1L);
        Mockito.when(requirementRepo.findByHoaId(1L)).thenReturn(Optional.of(list));
        Mockito.when(requirementRepo.save(requirement)).thenReturn(requirement);
        setUp();
        Assertions.assertEquals(requirement, requirementService.addHoaRequirement(1L, "Prompt"));
    }


    @Test
    void addHoaReqHappyWithNonEmpty() throws RequirementAlreadyPresent, HoaDoesntExistException {
        requirementRepo = Mockito.mock(RequirementRepo.class);
        List<Requirement> list = new ArrayList<>();
        Requirement requirement = new Requirement("Prompt", 1L);
        Requirement test = new Requirement("Test", 1L);
        list.add(test);
        Mockito.when(requirementRepo.findByHoaId(1L)).thenReturn(Optional.of(list));
        Mockito.when(requirementRepo.save(requirement)).thenReturn(requirement);
        setUp();
        Assertions.assertEquals(requirement, requirementService.addHoaRequirement(1L, "Prompt"));
    }

    @Test
    void addHoaReqHappyWithSamePromptDifferentHoaId() throws RequirementAlreadyPresent, HoaDoesntExistException {
        requirementRepo = Mockito.mock(RequirementRepo.class);
        List<Requirement> list = new ArrayList<>();
        Requirement requirement = new Requirement("Prompt", 1L);
        Requirement test = new Requirement("Prompt", 2L);
        list.add(test);
        Mockito.when(requirementRepo.findByHoaId(1L)).thenReturn(Optional.of(list));
        Mockito.when(requirementRepo.save(requirement)).thenReturn(requirement);
        setUp();
        Assertions.assertEquals(requirement, requirementService.addHoaRequirement(1L, "Prompt"));
    }

    @Test
    void removeHoaRequirementNotFound() {
        requirementRepo = Mockito.mock(RequirementRepo.class);
        Mockito.when(requirementRepo.findById(1L)).thenReturn(Optional.empty());
        setUp();
        Assertions.assertThrows(RequirementDoesNotExist.class, () -> requirementService.removeHoaRequirement(1L));
    }

    @Test
    void removeHoaRequirementHappy() throws RequirementDoesNotExist {
        requirementRepo = Mockito.mock(RequirementRepo.class);
        Requirement requirement = new Requirement("Prompt", 1L);
        Mockito.when(requirementRepo.findById(1L)).thenReturn(Optional.of(requirement));
        setUp();
        Assertions.assertEquals(requirement, requirementService.removeHoaRequirement(1L));
    }

}
