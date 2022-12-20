package voting.domain.factories;

import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.domain.Election;
import voting.domain.Proposal;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;
import voting.models.TimeModel;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = {UNIT})
class ProposalElectionFactoryTest {

    final transient ProposalElectionFactory sut = new ProposalElectionFactory();

    @Test
    void createElectionCompleteTest() {
        Proposal created = (Proposal) sut.createElection("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10));
        Proposal expected = new Proposal("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10));
        assertEquals(expected, created);
    }

    @Test
    void createElectionTest() {
        ProposalModel model = new ProposalModel();
        model.name = "a";
        model.description = "b";
        model.hoaId = 1;
        model.scheduledFor = new TimeModel(10, 10, 10, 10, 10, 10);
        Proposal created = (Proposal) sut.createElection(model);
        Proposal expected = new Proposal("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10));
        assertEquals(created, expected);

        model.hoaId = -1;
        Election createdNull = sut.createElection(model);
        assertNull(createdNull);

        BoardElectionModel wrongModel = new BoardElectionModel();
        wrongModel.name = "a";
        wrongModel.description = "b";
        wrongModel.hoaId = 1;
        wrongModel.scheduledFor = new TimeModel(10, 10, 10, 10, 10, 10);
        wrongModel.amountOfWinners = 2;
        wrongModel.candidates = List.of("test", "test2");
        Election wrongElection = sut.createElection(wrongModel);
        assertNull(wrongElection);
    }
}