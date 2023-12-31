package voting.db.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Converter
public class BoardElectionVotesConverter implements AttributeConverter<Map<String, String>, String> {

	@Override
	public String convertToDatabaseColumn(Map<String, String> attribute) {
		StringBuilder mapAsString = new StringBuilder();
		for (String key : attribute.keySet()) {
			mapAsString.append(key).append("=").append(attribute.get(key)).append(",");
		}
		if (mapAsString.length() != 0) mapAsString.deleteCharAt(mapAsString.length() - 1);
		return mapAsString.toString();
	}

	@Override
	public Map<String, String> convertToEntityAttribute(String dbData) {
		if (dbData.equals("")) return new HashMap<>();
		return Arrays.stream(dbData.split(","))
			.map(e -> e.split("="))
			.collect(Collectors.toMap(e -> e[0], e -> e[1]));
	}
}
