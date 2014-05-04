package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.util.Date;

public class PostIt implements Serializable {

    private static final long serialVersionUID = 2803787865875024468L;

    private Integer m_id;
    private Date m_date;
    private MinimalUser m_sender;
    private String m_text;
    private Boolean m_custom;
    private Boolean m_new;

    public Date getDate() {
        return m_date;
    }

    public void setDate(Date date) {
        m_date = date;
    }

    public MinimalUser getSender() {
        return m_sender;
    }

    public void setSender(MinimalUser sender) {
        m_sender = sender;
    }

    public String getText() {
        return m_text;
    }

    public void setText(String title) {
        m_text = title;
    }

    public Boolean isCustom() {
        return m_custom;
    }

    public void setCustom(Boolean custom) {
        m_custom = custom;
    }

    public Integer getId() {
        return m_id;
    }

    public void setId(Integer id) {
        m_id = id;
    }

    public Boolean isNew() {
        return m_new;
    }

    public void setNew(Boolean isNew) {
        m_new = isNew;
    }

}
