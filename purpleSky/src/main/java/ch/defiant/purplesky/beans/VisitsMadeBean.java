package ch.defiant.purplesky.beans;

import java.io.Serializable;

public class VisitsMadeBean extends AbstractVisitBean implements Serializable {

    private static final long serialVersionUID = 6763254501529810908L;

    private Boolean m_visible;

    public Boolean getVisible() {
        return m_visible;
    }

    public void setVisible(Boolean visible) {
        m_visible = visible;
    }

}
