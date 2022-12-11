package voting.db.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

@Converter
public class BoardElectionVotesConverter implements AttributeConverter<HashMap<Integer, Integer>, String> {

	@Override
	public String convertToDatabaseColumn(HashMap<Integer, Integer> attribute) {
		StringBuilder mapAsString = new StringBuilder();
		for (Integer key : attribute.keySet()) {
			mapAsString.append(key).append("=").append(attribute.get(key)).append(",");
		}
		if (mapAsString.length() != 0) mapAsString.deleteCharAt(mapAsString.length() - 1);
		return mapAsString.toString();
	}

	@Override
	public HashMap<Integer, Integer> convertToEntityAttribute(String dbData) {
		if (dbData.equals("")) return new HashMap<>();
		return (HashMap<Integer, Integer>) Arrays.stream(dbData.split(","))
			.map(e -> e.split("="))
			.collect(Collectors.toMap(e -> Integer.parseInt(e[0]), e -> Integer.parseInt(e[1])));
	}
}
