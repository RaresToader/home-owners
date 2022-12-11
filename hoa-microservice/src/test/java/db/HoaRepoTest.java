package db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HoaRepoTest {

    @Test
    public void testFindById() {
        Hoa mockHoa = Hoa.createHoa("Test", "Test city", "Test name");
        HoaRepo hoaRepo = mock(HoaRepo.class);
        when(hoaRepo.findById(1L)).thenReturn(Optional.of(mockHoa));
        Optional<Hoa> hoa = hoaRepo.findById(1);
        Assertions.assertTrue(hoa.isPresent());
        assertEquals(mockHoa, hoa.get());
    }

}
