package nl.tudelft.sem.template.hoa.db;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.domain.Requirement;
import nl.tudelft.sem.template.hoa.exception.BadFormatHoaException;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.HoaNameAlreadyTakenException;
import nl.tudelft.sem.template.hoa.models.HoaRequestModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * A DDD service for hoa-related queries.
 */
@Service
public class HoaService {
    private final transient HoaRepo hoaRepo;
    private final transient RequirementRepo requirementRepo;

    /**
     * Constructor for the HoaService.
     *
     * @param hoaRepo the hoa repository
     */
    public HoaService(HoaRepo hoaRepo, RequirementRepo requirementRepo) {
        this.hoaRepo = hoaRepo;
        this.requirementRepo = requirementRepo;
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
    public Hoa registerHoa(HoaRequestModel request) throws HoaNameAlreadyTakenException, BadFormatHoaException {
        String country = request.getCountry();
        String city = request.getCity();
        String name = request.getName();
        if (countryCheck(country) || countryCheck(city) || !nameCheck(name)) {
            throw new BadFormatHoaException("Wrong format for the country, city, or name!");
        }

        Hoa newHoa = Hoa.createHoa(country, city, name);
        this.saveHoa(newHoa);
        return newHoa;
    }

    /**
     * Method to report another member that violates one of the HOA rules
     *
     * @param memberId Id of member that violated an HOA rule
     * @param reqId    Id of requirement that was broken
     */
    public void report(String memberId, long reqId) throws ResponseStatusException {
        Optional<Requirement> req = requirementRepo.findById(reqId);
        if (req.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requirement has no associated HOA");
        Optional<Hoa> hoa = hoaRepo.findById(req.get().getHoaId());
        if (hoa.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Associated HOA does not exist");
        hoa.get().report(memberId, reqId);
        hoaRepo.save(hoa.get());
    }

    /**
     * Method to notify a member of a rule change/addition
     *
     * @param hoaId       id of HOA to consider
     * @param memberId    id of member to be notified
     * @param rulesChange String representing the rule that was changed/added
     */
    public void notify(long hoaId, String memberId, String rulesChange) throws
            ResponseStatusException {
        Optional<Hoa> hoa = hoaRepo.findById(hoaId);
        if (hoa.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Associated HOA does not exist");
        hoa.get().notify(memberId, rulesChange);
        hoaRepo.save(hoa.get());
    }

    /**
     * Method to clear notifications of a member
     *
     * @param hoaId    id of HOA to consider
     * @param memberId id of member to be notified
     */
    public List<String> clearNotifications(long hoaId, String memberId) throws HoaDoesntExistException {
        try {
            Hoa hoa = getHoaById(hoaId);
            List<String> res = hoa.resetNotifications(memberId);
            hoaRepo.save(hoa);
            return res;
        } catch (HoaDoesntExistException e) {
            throw new HoaDoesntExistException("Associated HOA does not exist");
        }
    }

    /**
     * This method checks that a country/city has the correct format.
     * This means that it must have maximum 50 characters, only letters (only ASCII), whitespaces are admissible,
     * no numbers and the first letter needs to be uppercase. Of course, the string cannot contain only whitespaces.
     *
     * @param country the country or city name
     * @return true if the country name fails to adhere to the format, false otherwise
     */
    public boolean countryCheck(String country) {
        return country == null || !country.matches("^[A-Z][a-zA-Z\\s]{3,49}$");
    }


    /**
     * Checks that a string has at least 4 characters.
     * Only letters or digits are allowed. The name can have at most 50 characters.
     *
     * @param name the name
     * @return true if the conditions are met, false otherwise.
     */
    public boolean nameCheck(String name) {
        return name != null && name.matches("^(?!\\s*$)[a-zA-Z0-9\\s]{4,50}$");
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
