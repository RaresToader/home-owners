package voting.db.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Converter
public class VotesConverter implements AttributeConverter<Map<Integer, Integer>, String> {

	@Override
	public String convertToDatabaseColumn(Map<Integer, Integer> attribute) {
		StringBuilder mapAsString = new StringBuilder();
		for (Integer key : attribute.keySet()) {
			mapAsString.append(key).append("=").append(attribute.get(key)).append(",");
		}
		if (mapAsString.length() != 0) mapAsString.deleteCharAt(mapAsString.length() - 1);
		return mapAsString.toString();
	}

	@Override
	public Map<Integer, Integer> convertToEntityAttribute(String dbData) {
		if (dbData.equals("")) return new HashMap<>();
		return Arrays.stream(dbData.split(","))
			.map(e -> e.split("="))
			.collect(Collectors.toMap(e -> Integer.parseInt(e[0]), e -> Integer.parseInt(e[1])));
	}
}
