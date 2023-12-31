package nl.tudelft.sem.template.hoa.db;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.INTEGRATION;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.db.RequirementRepo;
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

@TestSuite(testType = INTEGRATION)
public class HoaServiceTest {

    private final transient String testCity = "Test country";

    private final transient String testCountry = "Test city";

    private final transient String test = "Test";

    @Mock
    private transient HoaRepo hoaRepo;

    @Mock
    private transient RequirementRepo reqRepo;

    private transient HoaService hoaService;
    private final transient Hoa hoa = Hoa.createHoa(testCountry, testCity, test);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hoaService = new HoaService(hoaRepo, reqRepo);
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
                hoaService.saveHoa(Hoa.createHoa("Test2", "Test2", test)));
    }

    @Test
    void registerHoa() throws HoaNameAlreadyTakenException, BadFormatHoaException {
        HoaRequestModel model = new HoaRequestModel(testCountry, testCity, test);
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
    void nameCheckNull() {
        Assertions.assertFalse(hoaService.nameCheck(null));

    }

    @Test
    void nameCheckEmpty() {
        Assertions.assertFalse(hoaService.nameCheck(""));

    }

    @Test
    void nameCheckBlank() {
        Assertions.assertFalse(hoaService.nameCheck("    "));

    }

    @Test
    void nameCheckOtherChar() {
        Assertions.assertFalse(hoaService.nameCheck("Test 1$23"));

    }

    @Test
    void nameCheckNotUpper() {
        Assertions.assertFalse(hoaService.nameCheck("a"));
    }



    @Test
    void nameCheckHappy() {
        Assertions.assertTrue(hoaService.nameCheck("Test 123"));
    }

    @Test
    void nameCheckHappyButNotEnoughChars() {
        Assertions.assertFalse(hoaService.nameCheck("Tes"));
    }

    @Test
    void registerHoaInvalidCountry() {
        assertThrows(BadFormatHoaException.class,
                () -> hoaService.registerHoa(new HoaRequestModel("Tes$t country", testCity, test)));
    }

    @Test
    void registerHoaInvalidCity() {
        assertThrows(BadFormatHoaException.class,
                () -> hoaService.registerHoa(new HoaRequestModel(testCountry, "Test ci$ty", test)));
    }

    @Test
    void registerHoaInvalidName() {
        assertThrows(BadFormatHoaException.class,
                () -> hoaService.registerHoa(new HoaRequestModel("Test country", testCity, "Tst")));
    }

    @Test
    void countryCheckNotUpper() {
        Assertions.assertTrue(hoaService.countryCheck("aaaa"));
    }

    @Test
    void countryCheckLow() {
        Assertions.assertTrue(hoaService.countryCheck("Aa"));
    }

    @Test
    void countryCheckHigh() {
        Assertions.assertTrue(hoaService.countryCheck("Aa" + "a".repeat(60)));
    }

    @Test
    void nameCheckUpperCase() {
        Assertions.assertFalse(hoaService.nameCheck("aa"));
    }

    @Test
    void nameCheckUpper() {
        Assertions.assertFalse(hoaService.nameCheck("Aa" + "a".repeat(60)));
    }


}
