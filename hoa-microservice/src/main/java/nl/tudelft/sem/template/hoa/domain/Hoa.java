package nl.tudelft.sem.template.hoa.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.NoArgsConstructor;



/**
 * DDD entity representing an association in our domain.
 */
@Entity
@Table(name = "Hoa")
@NoArgsConstructor
public class Hoa {

    /**
     * Identifier for an HOA in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "name", nullable = false, unique = true)
    private String name;


    public long getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    /**
     * Private constructor for the HOA class.
     *
     * @param country the country of the HOA
     * @param city    the city of the HOA
     * @param name    the name of the HOA
     */
    private Hoa(String country, String city, String name) {
        this.country = country;
        this.city = city;
        this.name = name;
    }

    /**
     * Public method that can be called to create an HOA.
     *
     * @param country the country of the HOA
     * @param city    the city of the HOA
     * @param name    the name of the HOA
     * @return an HOA
     */
    public static Hoa createHoa(String country, String city, String name) {
        return new Hoa(country, city, name);
    }

}
