package ch.defiant.purplesky.core;

import java.util.Date;

public class AdapterOptions {

    private Integer m_start;
    private Integer m_number;
    private Date m_sinceTimestamp;
    private Date m_uptoTimestamp;
    private Long m_sinceId;
    private Long m_uptoId;

    private String m_userobjType;
    private String m_order;

    public Integer getStart() {
        return m_start;
    }

    public Integer getNumber() {
        return m_number;
    }

    public Date getSinceTimestamp() {
        return m_sinceTimestamp;
    }

    public Date getUptoTimestamp() {
        return m_uptoTimestamp;
    }

    public Long getSinceId() {
        return m_sinceId;
    }

    public Long getUptoId() {
        return m_uptoId;
    }

    public AdapterOptions setStart(Integer start) {
        m_start = start;
        return this;
    }

    public AdapterOptions setNumber(Integer number) {
        m_number = number;
        return this;
    }

    public AdapterOptions setSinceTimestamp(Date since) {
        m_sinceTimestamp = since;
        return this;
    }

    public AdapterOptions setUptoTimestamp(Date upto) {
        m_uptoTimestamp = upto;
        return this;
    }

    public AdapterOptions setSinceId(Long sinceId) {
        m_sinceId = sinceId;
        return this;
    }

    public AdapterOptions setUptoId(Long uptoId) {
        m_uptoId = uptoId;
        return this;
    }

    public String getUserobjType() {
        return m_userobjType;
    }

    public void setUserobjType(String userobjType) {
        m_userobjType = userobjType;
    }

    public String getOrder() {
        return m_order;
    }

    public void setOrder(String order) {
        m_order = order;
    }

}
