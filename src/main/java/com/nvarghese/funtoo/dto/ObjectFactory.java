package com.nvarghese.funtoo.dto;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: generated
	 * 
	 */
	public ObjectFactory() {

	}

	public ArticleDTO createArticleDTO() {

		return new ArticleDTO();
	}

	public UserDTO createUserDTO() {

		return new UserDTO();
	}
}
