package nl.tudelft.sem.template.hoa.db;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Converter
public class NotificationsConverter implements AttributeConverter<Map<String, List<String>>, String> {

	@Override
	public String convertToDatabaseColumn(Map<String, List<String>> attribute) {
		StringBuilder mapAsString = new StringBuilder();
		for (String key : attribute.keySet()) {
			mapAsString.append(key).append("=[");
			for (String r : attribute.get(key)) {
				mapAsString.append(r).append(",");
			}
			mapAsString.deleteCharAt(mapAsString.length() - 1).append("],");
		}
		if (mapAsString.length() != 0) mapAsString.deleteCharAt(mapAsString.length() - 1);
		return mapAsString.toString();
	}

	@Override
	public Map<String, List<String>> convertToEntityAttribute(String dbData) {
		if (dbData.equals("")) return new HashMap<>();
		Pattern p = Pattern.compile("(\\w+=\\[[0-9,]+])");
		Matcher m = p.matcher(dbData);
		Map<String, List<String>> res = new HashMap<>();
		while (m.find()) {
			String[] split = m.group(1).split("=");
			res.put(split[0], Arrays.stream(split[1].substring(1, split[1].length() - 1).split(","))
					.collect(Collectors.toList()));
		}
		return res;
	}
}
