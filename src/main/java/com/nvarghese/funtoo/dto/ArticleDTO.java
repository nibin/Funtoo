package com.nvarghese.funtoo.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "id", "title", "text", "belongsTo" })
@XmlRootElement(name = "article")
public class ArticleDTO {

	@XmlElement
	private Integer id;
	@XmlElement
	private String title;
	@XmlElement
	private String text;
	@XmlElement(name = "belongs_to")
	private int belongsTo;

	public Integer getId() {

		return id;
	}

	public void setId(Integer id) {

		this.id = id;
	}

	public int getBelongsTo() {

		return belongsTo;
	}

	public void setBelongsTo(int belongsTo) {

		this.belongsTo = belongsTo;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public String getText() {

		return text;
	}

	public void setText(String text) {

		this.text = text;
	}

}
