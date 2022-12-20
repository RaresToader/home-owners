package db;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.HoaNameAlreadyTakenException;
import nl.tudelft.sem.template.hoa.models.HoaRequestModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HoaServiceTest {

    @Mock
    private transient HoaRepo hoaRepo;

    private transient HoaService hoaService;
    private final transient Hoa hoa = Hoa.createHoa("Test country", "Test city", "Test");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hoaService = new HoaService(hoaRepo);
    }

    @Test
    void constructorTest() {
        Assertions.assertNotNull(hoaService);
    }


    @Test
    void getHoaByIdTest() throws HoaDoesntExistException {
        when(hoaRepo.findById(anyLong())).thenReturn(Optional.of(hoa));
        Assertions.assertEquals(hoa, hoaService.getHoaById(1L));
    }

    @Test
    void getActivityById_notFoundTest() {
        when(hoaRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(HoaDoesntExistException.class, () -> hoaService.getHoaById(1L));
    }

    @Test
    void getAllHoaTest() {
        List<Hoa> hoaList = new ArrayList<>();
        hoaList.add(hoa);
        when(hoaRepo.findAll()).thenReturn(hoaList);
        Assertions.assertEquals(hoaList, hoaService.getAllHoa());
    }

    @Test
    void findHoaByIdTest() {
        when(hoaRepo.existsById(1L)).thenReturn(true);
        Assertions.assertTrue(hoaService.findHoaById(1L));
    }

    @Test
    void saveHoaTest() throws HoaNameAlreadyTakenException {
        List<Hoa> list = new ArrayList<>();
        when(hoaRepo.findAll()).thenReturn(list);
        hoaService.saveHoa(hoa);
        verify(hoaRepo).save(hoa);
    }

    @Test
    void saveHoaTestFail() {
        List<Hoa> list = new ArrayList<>();
        list.add(hoa);
        when(hoaRepo.findAll()).thenReturn(list);
        assertThrows(HoaNameAlreadyTakenException.class, () ->
                hoaService.saveHoa(Hoa.createHoa("Test2", "Test2", "Test")));
    }

    @Test
    void registerHoa() throws HoaNameAlreadyTakenException {
        HoaRequestModel model = new HoaRequestModel("Test country", "Test city", "Test");
        Assertions.assertEquals(hoaService.registerHoa(model), hoa);
    }

}
