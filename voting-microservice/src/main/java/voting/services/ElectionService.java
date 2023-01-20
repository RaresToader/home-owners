package voting.services;

import org.springframework.stereotype.Service;
import voting.domain.BoardElection;
import voting.domain.factories.BoardElectionFactory;
import voting.domain.Election;
import voting.domain.Proposal;
import voting.db.repos.ElectionRepository;
import voting.domain.factories.ProposalElectionFactory;
import voting.exceptions.BoardElectionAlreadyCreated;
import voting.exceptions.CannotProceedVote;
import voting.exceptions.ElectionCannotBeCreated;
import voting.exceptions.ElectionDoesNotExist;
import voting.exceptions.ProposalAlreadyCreated;
import voting.exceptions.ThereIsNoVote;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;
import voting.models.RemoveVoteModel;
import voting.models.VotingModel;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
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
        if (model.scheduledFor.createDate().isBefore(LocalDateTime.now()))
            throw new ElectionCannotBeCreated("Election cannot be scheduled in the past");
        if (electionRepository.getBoardElectionByHoaId(model.hoaId).isEmpty()) {
            BoardElection boardElection = (BoardElection) new BoardElectionFactory().createElection(model);
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
            Proposal proposal = (Proposal) new ProposalElectionFactory().createElection(model);
            electionRepository.save(proposal);
            return proposal;
        }
    }


    /**
     * Creates a proposal
     *
     * @param model Model from which to create the proposal
     * @return New proposal, if one does not exist with same hoaID and name
     * @throws ProposalAlreadyCreated If proposal for the given HoaID and name already exists
     */
    public Proposal createProposal(ProposalModel model, TemporalAmount startAfter)
                                                throws ProposalAlreadyCreated, ElectionCannotBeCreated {
        if (!model.isValid()) throw new ElectionCannotBeCreated("Some/all of the provided fields are invalid");
        if (electionRepository.existsByHoaIdAndName(model.hoaId, model.name))
            throw new ProposalAlreadyCreated("Proposal with hoaId: "
                + model.hoaId
                + "and name: "
                + model.name
                + "already exists.");
        else {
            Proposal proposal = (Proposal) new ProposalElectionFactory().createElection(model, startAfter);
            electionRepository.save(proposal);
            return proposal;
        }
    }

    /**
     * Checks whether an election is ongoing in the current moment
     * @param election - the election for which the check is
     * @param currTime - the current moment
     * @throws CannotProceedVote - if the election is finished or not started
     */
    private void checkElectionTime(Election election, LocalDateTime currTime) throws CannotProceedVote {
        if (election.getScheduledFor().isAfter(currTime))
            throw new CannotProceedVote("Election has not started");
        if (election.getStatus().equals("finished"))
            throw new CannotProceedVote("Election has been concluded");
    }

    /**
     * Checks whether the choice is a legal choice for the given election
     * @param election - the election that we check
     * @param choice - the choice that we check
     * @throws CannotProceedVote - if the vote is illegal
     */
    private void checkElectionVote(Election election, String choice) throws CannotProceedVote {

        if (election.getClass() == BoardElection.class) {
            if (!((BoardElection) election).getCandidates().contains(choice))
                throw new CannotProceedVote("Candidate with given id is not nominated for the election");
        } else if (!List.of("True", "true", "T", "False", "false", "F").contains(choice))
            throw new CannotProceedVote("Invalid voting choice for proposal (must be a boolean or similar)");
    }

    /**
     * Method called when a member wants to vote
     *
     * @param model VotingModel that contains electionId, memberID, and voting choice
     * @throws ElectionDoesNotExist If election does not exist with provided id
     */
    public int vote(VotingModel model, LocalDateTime currTime) throws ElectionDoesNotExist, CannotProceedVote {
        if (!model.isValid())
            throw new ElectionDoesNotExist("Ids not valid");
        Optional<Election> election = this.electionRepository.findByElectionId(model.electionId);
        if (election.isEmpty())
            throw new ElectionDoesNotExist("Election not found");
        checkElectionTime(election.get(), currTime);
        checkElectionVote(election.get(), model.choice);
        election.get().setStatus("ongoing");
        election.get().vote(model.memberId, model.choice);
        Election e = this.electionRepository.save(election.get());
        return e.getVoteCount();
    }

    /**
     * Method called when a member wants to remove his vote
     *
     * @param model RemoveVoteModel that contains electionId and memberID
     * @throws ElectionDoesNotExist If election does not exist with provided id
     * @throws ThereIsNoVote If the member has not voted yet
     * @throws CannotProceedVote If the request has been made before the beginning or after the end of the voting process
     */
    public int removeVote(RemoveVoteModel model, LocalDateTime currTime)
                            throws ElectionDoesNotExist, ThereIsNoVote, CannotProceedVote {
        if (!model.isValid())
            throw new ElectionDoesNotExist("Ids not valid");
        Optional<Election> election = this.electionRepository.findByElectionId(model.electionId);
        if (election.isEmpty())
            throw new ElectionDoesNotExist("Election not found");
        checkElectionTime(election.get(), currTime);
        election.get().removeVote(model.memberId);
        Election e = this.electionRepository.save(election.get());
        return e.getVoteCount();
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
        if (e.getCandidates().contains(memberId)) return false;
        e.addParticipant(memberId);
        electionRepository.save(e);
        return true;
    }

    /**
     * Removes participant from board election if there is a board election and the member is participating
     */
    public boolean removeParticipantFromBoardElection(String memberId, long hoaId) throws ElectionDoesNotExist {
        BoardElection e = getBoardElectionByHoaId(hoaId);
        if (e.removeParticipant(memberId)) {
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
