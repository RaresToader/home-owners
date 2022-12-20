package voting.services;

import org.springframework.stereotype.Service;
import voting.domain.BoardElection;
import voting.domain.Election;
import voting.domain.Proposal;
import voting.db.repos.ElectionRepository;
import voting.exceptions.BoardElectionAlreadyCreated;
import voting.exceptions.CannotProceedVote;
import voting.exceptions.ElectionCannotBeCreated;
import voting.exceptions.ElectionDoesNotExist;
import voting.exceptions.ProposalAlreadyCreated;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;
import voting.models.VotingModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ElectionService {
    private final transient ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    /**
     * Creates a board election
     *
     * @param model Model from which to create the election
     * @return New board election, if one does not exist with same hoaID
     * @throws BoardElectionAlreadyCreated If board election for the given HoaID already exists
     */
    public BoardElection createBoardElection(BoardElectionModel model)
            throws BoardElectionAlreadyCreated, ElectionCannotBeCreated {
        if (!model.isValid()) throw new ElectionCannotBeCreated("Some/all of the provided fields are invalid");
        if (electionRepository.getBoardElectionByHoaId(model.hoaId).isEmpty()) {
            LocalDateTime d = model.scheduledFor.createDate();
            BoardElection boardElection = new BoardElection(model.name, model.description, model.hoaId, d,
                    model.amountOfWinners, model.candidates);
            electionRepository.save(boardElection);
            return boardElection;
        } else throw new BoardElectionAlreadyCreated("Board election with hoaId: "
                + model.hoaId
                + "already exists.");
    }

    /**
     * Creates a proposal
     *
     * @param model Model from which to create the proposal
     * @return New proposal, if one does not exist with same hoaID and name
     * @throws ProposalAlreadyCreated If proposal for the given HoaID and name already exists
     */
    public Proposal createProposal(ProposalModel model) throws ProposalAlreadyCreated, ElectionCannotBeCreated {
        if (!model.isValid()) throw new ElectionCannotBeCreated("Some/all of the provided fields are invalid");
        if (electionRepository.existsByHoaIdAndName(model.hoaId, model.name))
            throw new ProposalAlreadyCreated("Proposal with hoaId: "
                    + model.hoaId
                    + "and name: "
                    + model.name
                    + "already exists.");
        else {
            LocalDateTime d = model.scheduledFor.createDate();
            Proposal proposal = new Proposal(model.name, model.description, model.hoaId, d);
            electionRepository.save(proposal);
            return proposal;
        }
    }

    /**
     * Method called when a member wants to vote
     *
     * @param model VotingModel that contains electionId, memberID, and voting choice
     * @throws ElectionDoesNotExist If election does not exist with provided id
     */
    public void vote(VotingModel model, LocalDateTime currTime) throws ElectionDoesNotExist, CannotProceedVote {
        if (!model.isValid())
            throw new ElectionDoesNotExist("Ids not valid");
        Optional<Election> election = this.electionRepository.findByElectionId(model.electionId);
        if (election.isEmpty())
            throw new ElectionDoesNotExist("Election not found");
        if (election.get().getScheduledFor().isAfter(currTime))
            throw new CannotProceedVote("Election has not started");
        if (election.get().getStatus().equals("finished"))
            throw new CannotProceedVote("Election has been concluded");
        if (election.get().getClass() == BoardElection.class
                && !((BoardElection) election.get()).getCandidates().contains(model.choice))
            throw new CannotProceedVote("Candidate with given id is not nominated for the election");
        if (election.get().getClass() == Proposal.class
                && (List.of("True", "true", "T", "False", "false", "F").contains(model.choice)))
            throw new CannotProceedVote("Candidate with given id is not nominated for the election");
        election.get().setStatus("ongoing");
        election.get().vote(model.memberId, model.choice);
        this.electionRepository.save(election.get());
    }

    /**
     * Gets an election with the given id
     *
     * @param electionId Id of election to fetch
     * @return Fetched election, if it exists
     * @throws ElectionDoesNotExist If an election with given id does not exist
     */
    public Election getElection(int electionId) throws ElectionDoesNotExist {
        Optional<Election> e = this.electionRepository.findByElectionId(electionId);
        if (e.isEmpty()) throw new ElectionDoesNotExist("Election with provided id does not exist");
        return e.get();
    }

    /**
     * Returns board election for a given hoa, if one is running
     */
    public BoardElection getBoardElectionByHoaId(long hoaId) throws ElectionDoesNotExist {
        Optional<Election> e = this.electionRepository.getBoardElectionByHoaId(hoaId);
        if (e.isEmpty()) throw new ElectionDoesNotExist("This hoa does not have an election");
        return (BoardElection) e.get();
    }

    /**
     * Adds a participant to board election if there is an election
     */
    public boolean addParticipantToBoardElection(String memberId, long hoaId) throws ElectionDoesNotExist {
        BoardElection e = getBoardElectionByHoaId(hoaId);
        e.addParticipant(memberId);
        electionRepository.deleteById(e.getElectionId());
        electionRepository.save(e);
        return true;
    }

    /**
     * Removes participant from board election if there is a board election and the member is participating
     */
    public boolean removeParticipantFromBoardElection(String memberId, long hoaId) throws ElectionDoesNotExist {
        BoardElection e = getBoardElectionByHoaId(hoaId);
        if(e.removeParticipant(memberId)) {
            electionRepository.deleteById(e.getElectionId());
            electionRepository.save(e);
            return true;
        }
        return false;
    }

    /**
     * Concludes an election with the given id
     *
     * @param electionId Id of election to conclude
     * @return Result of the election
     * @throws ElectionDoesNotExist If an election with provided id does not exist
     */
    public Object conclude(int electionId) throws ElectionDoesNotExist {
        Optional<Election> e = this.electionRepository.findByElectionId(electionId);
        if (e.isEmpty()) throw new ElectionDoesNotExist("Election with provided id does not exist");
        Object res = e.get().conclude();
        this.electionRepository.save(e.get());
        return res;
    }
}
