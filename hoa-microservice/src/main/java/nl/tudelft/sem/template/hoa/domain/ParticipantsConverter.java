package nl.tudelft.sem.template.hoa.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;

public class ParticipantsConverter implements AttributeConverter<List<Long>, String> {

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        String result = "";
        for (Long l : attribute) {
            result += l + ",";
        }
        if (result.length() != 0) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

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