package db;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.exception.BadFormatHoaException;
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
        assertTrue(hoaService.findHoaById(1L));
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
    void registerHoa() throws HoaNameAlreadyTakenException, BadFormatHoaException {
        HoaRequestModel model = new HoaRequestModel("Test country", "Test city", "Test");
        Assertions.assertEquals(hoaService.registerHoa(model), hoa);
    }

    @Test
    void countryCheckNull() {
        assertTrue(hoaService.countryCheck(null));
    }

    @Test
    void countryCheckEmpty() {
        assertTrue(hoaService.countryCheck(""));
    }

    @Test
    void countryCheckBlank() {
        assertTrue(hoaService.countryCheck("     "));

    }

    @Test
    void countryNotUpper() {
        assertTrue(hoaService.countryCheck("a"));
    }

    @Test
    void otherChar() {
        assertTrue(hoaService.countryCheck("Australia $$"));

    }

    @Test
    void correctFormat() {
        Assertions.assertFalse(hoaService.countryCheck("Australia"));
    }

    @Test
    void nullName() {
        Assertions.assertFalse(hoaService.nameCheck(null));
    }

    @Test
    void emptyName() {
        Assertions.assertFalse(hoaService.nameCheck(""));
    }

    @Test
    void blankName() {
        Assertions.assertFalse(hoaService.nameCheck("    "));
    }

    @Test
    void nameNotUpper() {
        Assertions.assertFalse(hoaService.nameCheck("a"));
    }

    @Test
    void happyName() {
        Assertions.assertTrue(hoaService.nameCheck("Test name"));
    }

    @Test
    void enoughCharsAndWhitespaceNull() {
        Assertions.assertFalse(hoaService.enoughCharsAndWhitespace(null));

    }

    @Test
    void enoughCharsAndWhitespaceEmpty() {
        Assertions.assertFalse(hoaService.enoughCharsAndWhitespace(""));

    }

    @Test
    void enoughCharsAndWhitespaceBlank() {
        Assertions.assertFalse(hoaService.enoughCharsAndWhitespace("    "));

    }

    @Test
    void enoughCharsAndWhitespaceOtherChar() {
        Assertions.assertFalse(hoaService.enoughCharsAndWhitespace("Test 1$23"));

    }

    @Test
    void enoughCharsAndWhitespaceHappy() {
        Assertions.assertTrue(hoaService.enoughCharsAndWhitespace("Test 123"));
    }

    @Test
    void enoughCharsAndWhitespaceHappyButNotEnoughChars() {
        Assertions.assertFalse(hoaService.enoughCharsAndWhitespace("Tes"));
    }

    @Test
    void registerHoaInvalidCountry() {
        HoaRequestModel model = new HoaRequestModel("Tes$t country", "Test city", "Test");
        assertThrows(BadFormatHoaException.class, () -> hoaService.registerHoa(model));
    }

    @Test
    void registerHoaInvalidCity() {
        HoaRequestModel model = new HoaRequestModel("Test country", "Test ci$ty", "Test");
        assertThrows(BadFormatHoaException.class, () -> hoaService.registerHoa(model));
    }

    @Test
    void registerHoaInvalidName() {
        HoaRequestModel model = new HoaRequestModel("Test country", "Test city", "Tst");
        assertThrows(BadFormatHoaException.class, () -> hoaService.registerHoa(model));
    }



}
