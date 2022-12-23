package nl.tudelft.sem.template.hoa.domain;

import nl.tudelft.sem.template.hoa.annotations.Generated;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;

/**
 * Converter class used to convert the list of participants into a String and vice-versa.
 */
@Generated
public class ParticipantsConverter implements AttributeConverter<List<Long>, String> {
    /**
     * Converts a list of participants into a string.
     *
     * @param attribute the list of participants
     * @return the string with the participants separated by whitespace
     */
    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        StringBuilder result = new StringBuilder();
        for (Long l : attribute) {
            result.append(l).append(",");
        }
        if (result.length() != 0) {
            result = new StringBuilder(result.substring(0, result.length() - 1));
        }
        return result.toString();
    }

    /**
     * Reconverts a list of participants from a string.
     *
     * @param dbData the string from the database column
     * @return a list of longs, corresponding to participants
     */
    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        List<Long> list = new ArrayList<>();
        if (dbData.isEmpty()) {
            return list;
        }
        String[] data = dbData.split(",");
        for (String s : data) {
            list.add(Long.parseLong(s));
        }
        return list;
    }

}