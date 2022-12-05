package voting.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardElectionModel extends ElectionModel{

	private int amountOfWinners;
	private ArrayList<Integer> candidates;

	public int getAmountOfWinners() {
		return amountOfWinners;
	}

	public ArrayList<Integer> getCandidates() {
		return candidates;
	}
}
