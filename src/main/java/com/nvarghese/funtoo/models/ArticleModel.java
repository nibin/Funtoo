package com.nvarghese.funtoo.models;

public class ArticleModel {

	private int id;
	private String title;
	private String text;
	private int belongsTo;

	public int getId() {

		return id;
	}

	public void setId(int id) {

		this.id = id;
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

	public int getBelongsTo() {

		return belongsTo;
	}

	public void setBelongsTo(int belongsTo) {

		this.belongsTo = belongsTo;
	}

}
