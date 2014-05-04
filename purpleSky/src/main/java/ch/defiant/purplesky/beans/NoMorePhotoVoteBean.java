package ch.defiant.purplesky.beans;

public class NoMorePhotoVoteBean extends PhotoVoteBean {

    private static final long serialVersionUID = 3701591189982907703L;

    @Override
    public Integer getVotesRemaining() {
        return 0;
    }

}
