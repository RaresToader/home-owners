package voting.db.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

@Converter
public class VotesConverter<T>  implements AttributeConverter<HashMap<Integer, T>, String> {

	@Override
	public String convertToDatabaseColumn(HashMap<Integer, T> attribute) {
		StringBuilder mapAsString = new StringBuilder();
		for (Integer key : attribute.keySet()) {
			mapAsString.append(key).append("=").append(attribute.get(key)).append(",");
		}
		mapAsString.deleteCharAt(mapAsString.length() - 1);
		return mapAsString.toString();
	}

	@Override
	public HashMap<Integer, T> convertToEntityAttribute(String dbData) {
		return (HashMap<Integer, T>) Arrays.stream(dbData.split(","))
			.map(e -> e.split("="))
			.collect(Collectors.toMap(e -> Integer.parseInt(e[0]), e -> {
				try {
					return Boolean.parseBoolean(e[1]);
				} catch (Exception ignored) {
					return Integer.parseInt(e[1]);
				}
			}));
	}
}
