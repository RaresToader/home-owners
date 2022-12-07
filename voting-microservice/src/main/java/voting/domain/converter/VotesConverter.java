package voting.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Converter
public class VotesConverter implements AttributeConverter<Map<String, Integer>, String> {

	@Override
	public String convertToDatabaseColumn(Map<String, Integer> attribute) {
		StringBuilder mapAsString = new StringBuilder();
		for (String key : attribute.keySet()) {
			mapAsString.append(key + "=" + attribute.get(key) + ", ");
		}
		mapAsString.delete(mapAsString.length()-2, mapAsString.length());
		return mapAsString.toString();
	}

	@Override
	public Map<String, Integer> convertToEntityAttribute(String dbData) {
		Map<String, Integer> map = Arrays.stream(dbData.split(","))
			.map(entry -> entry.split("="))
			.collect(Collectors.toMap(entry -> entry[0], entry -> Integer.parseInt(entry[1])));
		return map;
	}
}
