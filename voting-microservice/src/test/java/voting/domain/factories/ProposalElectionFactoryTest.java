package voting.domain.factories;

import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.domain.Election;
import voting.domain.Proposal;
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
        ProposalModel model = new ProposalModel("a", "b", 1,
                new TimeModel(10, 10, 10, 10, 10, 10));
        Proposal created = (Proposal) sut.createElection(model);
        Proposal expected = new Proposal("a", "b", 1,
                LocalDateTime.of(10, 10, 10, 10, 10, 10));
        assertEquals(created, expected);

        model = new ProposalModel(model.name, model.description, -1, model.scheduledFor);
        Election createdNull = sut.createElection(model);
        assertNull(createdNull);

        BoardElectionModel wrongModel = new BoardElectionModel(new ElectionModel("a", "b", 1,
                        new TimeModel(10, 10, 10, 10, 10, 10)),
                2, List.of("test", "test2"));
        Election wrongElection = sut.createElection(wrongModel);
        assertNull(wrongElection);
    }

}