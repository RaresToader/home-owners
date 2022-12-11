package nl.tudelft.sem.template.authmember.models;

public abstract class HoaModel {
    private transient String memberId;
    private transient long hoaId;

    public String getMemberId() {
        return memberId;
    }

    public long getHoaId() {
        return hoaId;
    }
}
