package voting.domain.converter;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;

public class CandidatesConverter implements AttributeConverter<ArrayList<Integer>, String> {

	@Override
	public String convertToDatabaseColumn(ArrayList<Integer> candidates) {
		String result = "";
		for (int l : candidates) {
			result += l + ",";
		}
		if (result.length() != 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	@Override
	public ArrayList<Integer> convertToEntityAttribute(String dbData) {
		ArrayList<Integer> list = new ArrayList<>();
		if (dbData.isEmpty()) {
			return list;
		}
		String[] data = dbData.split(",");
		for (String s : data) {
			list.add(Integer.parseInt(s));
		}
		return list;
	}
}
