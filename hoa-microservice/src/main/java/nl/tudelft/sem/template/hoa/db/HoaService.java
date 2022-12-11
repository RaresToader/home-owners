package nl.tudelft.sem.template.hoa.db;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.HoaNameAlreadyTakenException;
import nl.tudelft.sem.template.hoa.models.HoaRequestModel;
import org.springframework.stereotype.Service;

/**
 * A DDD service for hoa-related queries.
 */
@Service
public class HoaService {
    private final transient HoaRepo hoaRepo;

    /**
     * Constructor for the HoaService.
     *
     * @param hoaRepo the hoa repository
     */
    public HoaService(HoaRepo hoaRepo) {
        this.hoaRepo = hoaRepo;
    }

    /**
     * Query to save a Hoa.
     *
     * @param hoa the hoa to be saved
     * @throws HoaNameAlreadyTakenException if the name already exists
     */
    public void saveHoa(Hoa hoa) throws HoaNameAlreadyTakenException {
        List<String> list = hoaRepo.findAll().stream().map(Hoa::getName).collect(Collectors.toList());
        if (list.contains(hoa.getName())) {
            throw new HoaNameAlreadyTakenException("Hoa with name " + hoa.getName() + " is already taken.");
        }
        this.hoaRepo.save(hoa);
    }

    /**
     * Query to retrieve all hoa from the database.
     *
     * @return a list of a hoa's
     */
    public List<Hoa> getAllHoa() {
        return hoaRepo.findAll();
    }

    /**
     * Query to get a hoa by its id.
     *
     * @param id the id of the queried hoa
     * @return the hoa in case it exists
     * @throws HoaDoesntExistException if there does not exist a hoa with the specified id.
     */
    public Hoa getHoaById(long id) throws HoaDoesntExistException {
        Optional<Hoa> hoa = hoaRepo.findById(id);
        if (hoa.isEmpty()) {
            throw new HoaDoesntExistException("Hoa with id " + id + " doesn't exist.");
        }
        return hoa.get();
    }

    /**
     * Method to create a new Hoa.
     *
     * @param request a hoa request model
     * @return the hoa newly created
     * @throws HoaNameAlreadyTakenException thrown if the hoa name already exists
     */
    public Hoa registerHoa(HoaRequestModel request) throws HoaNameAlreadyTakenException {
        String country = request.getCountry();
        String city = request.getCity();
        String name = request.getName();
        Hoa newHoa = Hoa.createHoa(country, city, name);
        this.saveHoa(newHoa);
        return newHoa;
    }

    /**
     * Checks whether a Hoa exists.
     *
     * @param hoaId the hoaId
     * @return true if the hoa exists, false otherwise
     */
    public boolean findHoaById(long hoaId) {
        return hoaRepo.existsById(hoaId);
    }
}
