package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "ADMINS")
public class AdminDetails extends AbstractEntity {

	@NotNull
	@Length(min = 1, max = 32)
	private String firstname;
	@NotNull
	@Length(min = 1, max = 32)
	private String lastname;

	private String email;

	// FIXME Passwords should never be stored in plain text!
	@NotNull
	@Length(min = 8, max = 64)
	private String password;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}