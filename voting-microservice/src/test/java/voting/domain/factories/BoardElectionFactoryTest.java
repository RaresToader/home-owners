package voting.domain.factories;

import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.domain.BoardElection;
import voting.domain.Election;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;
import voting.models.TimeModel;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = {UNIT})
class BoardElectionFactoryTest {

    final transient BoardElectionFactory sut = new BoardElectionFactory();

    @Test
    void createElectionPartialTest() {
        BoardElection created = (BoardElection) sut.createElection("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10));
        BoardElection expected = new BoardElection("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10), 0,
                List.of());
        assertEquals(expected, created);
    }

    @Test
    void createElectionCompleteTest() {
        BoardElection created = (BoardElection) sut.createElection("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10),
                1, List.of(3, 4));
        BoardElection expected = new BoardElection("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10), 1,
                List.of(3, 4));
        assertEquals(expected, created);
    }

    @Test
    void createElectionTest() {
        BoardElectionModel model = new BoardElectionModel();
        model.name = "a";
        model.description = "b";
        model.hoaId = 1;
        model.scheduledFor = new TimeModel(10, 10, 10, 10, 10, 10);
        model.amountOfWinners = 2;
        model.candidates = List.of(3, 4);
        BoardElection created = (BoardElection) sut.createElection(model);
        BoardElection expected = new BoardElection("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10),
                2, List.of(3, 4));
        assertEquals(created, expected);

        model.hoaId = -1;
        Election createdNull = sut.createElection(model);
        assertNull(createdNull);

        ProposalModel wrongModel = new ProposalModel();
        wrongModel.name = "a";
        wrongModel.description = "b";
        wrongModel.hoaId = 1;
        wrongModel.scheduledFor = new TimeModel(10, 10, 10, 10, 10, 10);
        Election wrongElection = sut.createElection(wrongModel);
        assertNull(wrongElection);
    }
}