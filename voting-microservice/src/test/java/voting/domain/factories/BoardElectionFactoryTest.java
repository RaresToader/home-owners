package voting.domain.factories;

import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.domain.BoardElection;
import voting.domain.Election;
import voting.models.BoardElectionModel;
import voting.models.ElectionModel;
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
        BoardElection created = (BoardElection) sut.createElection(new ElectionModel("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10)),
                1, List.of("test", "test2"));
        BoardElection expected = new BoardElection("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10), 1,
                List.of("test", "test2"));
        assertEquals(expected, created);
    }

    @Test
    void createElectionTest() {
        BoardElectionModel model = new BoardElectionModel("a", "b", 1,
                new TimeModel(10, 10, 10, 10, 10, 10), 2,
                List.of("test", "test2"));
        BoardElection created = (BoardElection) sut.createElection(model);
        BoardElection expected = new BoardElection("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10),
                2, List.of("test", "test2"));
        assertEquals(created, expected);

        model = new BoardElectionModel(model.name, model.description, -1, model.scheduledFor,
                model.amountOfWinners, model.candidates);
        Election createdNull = sut.createElection(model);
        assertNull(createdNull);

        ProposalModel wrongModel = new ProposalModel("a", "b", 1,
                new TimeModel(10, 10, 10, 10, 10, 10));
        Election wrongElection = sut.createElection(wrongModel);
        assertNull(wrongElection);
    }
}