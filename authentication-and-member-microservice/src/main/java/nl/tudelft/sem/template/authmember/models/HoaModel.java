package nl.tudelft.sem.template.authmember.models;

public abstract class HoaModel {
    private transient String memberId;
    private transient int hoaId;

    public String getMemberId() {
        return memberId;
    }

    public  int getHoaId() {
        return hoaId;
    }
}
