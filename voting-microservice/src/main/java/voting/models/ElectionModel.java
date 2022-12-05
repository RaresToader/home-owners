package voting.models;

import lombok.Data;
import java.sql.Time;

@Data
public abstract class ElectionModel {

	private int electionId;
	private int hoaId;
	private String name;
	private String description;
	private Time scheduledFor;

	public int getElectionId() {
		return electionId;
	}

	public int getHoaId() {
		return hoaId;
	}

	public String getName() {
		return name;
	}

	public Time getScheduledFor() {
		return scheduledFor;
	}
}
