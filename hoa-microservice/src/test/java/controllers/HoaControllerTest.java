package controllers;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.hoa.controllers.HoaController;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.db.RequirementService;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.HoaNameAlreadyTakenException;
import nl.tudelft.sem.template.hoa.models.HoaRequestModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HoaControllerTest {
    @Test
    public void testRegister() throws HoaNameAlreadyTakenException {
        HoaRequestModel request = new HoaRequestModel();
        request.setName("Test HOA");
        request.setCity("Test City");
        request.setCountry("TS");
        Hoa newHoa = Hoa.createHoa("TS", "Test City", "Test HOA");
        HoaService hoaService = Mockito.mock(HoaService.class);
        Mockito.when(hoaService.registerHoa(request)).thenReturn(newHoa);
        RequirementService requirementService = Mockito.mock(RequirementService.class);
        HoaController controller = new HoaController(hoaService, requirementService);
        ResponseEntity<Hoa> response = controller.register(request, "joe");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), newHoa);
    }

    @Test
    public void testGetAll() {
        HoaService hoaService = Mockito.mock(HoaService.class);
        RequirementService requirementService = Mockito.mock(RequirementService.class);
        HoaController controller = new HoaController(hoaService, requirementService);
        Hoa hoa1 = Hoa.createHoa("TS", "Test City", "Test HOA1");
        Hoa hoa2 = Hoa.createHoa("TS", "Test City", "Test HOA2");
        List<Hoa> hoa = Arrays.asList(hoa1, hoa2);
        Mockito.when(hoaService.getAllHoa()).thenReturn(hoa);
        ResponseEntity<List<Hoa>> response = controller.getAll("joe");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), hoa);
    }

    @Test
    public void testGetById() throws HoaDoesntExistException {
        HoaService mockHoaService = Mockito.mock(HoaService.class);
        RequirementService mockRequirementService = Mockito.mock(RequirementService.class);
        HoaController controller = new HoaController(mockHoaService, mockRequirementService);
        Hoa hoa = Hoa.createHoa("TS", "Test City", "Test HOA");
        Mockito.when(mockHoaService.getHoaById(1L)).thenReturn(hoa);
        ResponseEntity<Hoa> response = controller.getById(1L, "joe");
        Assertions.assertEquals(response.getBody(), hoa);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}