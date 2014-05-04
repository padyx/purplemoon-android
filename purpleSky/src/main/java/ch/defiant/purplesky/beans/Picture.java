package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.util.Date;

public class Picture implements Serializable {

	private static final long serialVersionUID = 6103123572277984256L;

	private Integer m_pictureId;
	private Date m_date;
	private String m_url;
	private Integer m_maxWidth;
	private Integer m_maxHeight;

	public Integer getPictureId() {
		return m_pictureId;
	}

	public void setPictureId(Integer pictureId) {
		m_pictureId = pictureId;
	}

	public Date getDate() {
		return m_date;
	}

	public void setDate(Date date) {
		m_date = date;
	}

	public String getUrl() {
		return m_url;
	}

	public void setUrl(String url) {
		m_url = url;
	}

	public Integer getMaxWidth() {
		return m_maxWidth;
	}

	public void setMaxWidth(Integer maxWidth) {
		m_maxWidth = maxWidth;
	}

	public Integer getMaxHeight() {
		return m_maxHeight;
	}

	public void setMaxHeight(Integer maxHeight) {
		m_maxHeight = maxHeight;
	}

}
