package voting.db.converters;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CandidatesConverter implements AttributeConverter<List<Integer>, String> {

	@Override
	public String convertToDatabaseColumn(List<Integer> candidates) {
		StringBuilder sb = new StringBuilder();
		sb.append(candidates.stream().map(i -> i + ",").collect(Collectors.joining()));
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	@Override
	public List<Integer> convertToEntityAttribute(String dbData) {
		if (dbData.isEmpty()) return new ArrayList<>();
		return Arrays.stream(dbData.split(",")).map(Integer::parseInt).collect(Collectors.toList());
	}
}
