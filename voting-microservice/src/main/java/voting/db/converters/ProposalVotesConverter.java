package voting.db.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

@Converter
public class ProposalVotesConverter implements AttributeConverter<HashMap<Integer, Boolean>, String> {

	@Override
	public String convertToDatabaseColumn(HashMap<Integer, Boolean> attribute) {
		StringBuilder mapAsString = new StringBuilder();
		for (Integer key : attribute.keySet()) {
			mapAsString.append(key).append("=").append(attribute.get(key)).append(",");
		}
		mapAsString.deleteCharAt(mapAsString.length() - 1);
		return mapAsString.toString();
	}

	@Override
	public HashMap<Integer, Boolean> convertToEntityAttribute(String dbData) {
		return (HashMap<Integer, Boolean>) Arrays.stream(dbData.split(","))
			.map(e -> e.split("="))
			.collect(Collectors.toMap(e -> Integer.parseInt(e[0]), e -> Boolean.parseBoolean(e[1])));
	}
}
