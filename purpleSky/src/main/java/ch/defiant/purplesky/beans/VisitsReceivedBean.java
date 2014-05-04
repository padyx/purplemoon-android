package ch.defiant.purplesky.beans;

public class VisitsReceivedBean extends AbstractVisitBean {

    private static final long serialVersionUID = 3885260271325440368L;

    private boolean m_unseen;

    public boolean isUnseen() {
        return m_unseen;
    }

    public void setUnseen(boolean unseen) {
        m_unseen = unseen;
    }

}
