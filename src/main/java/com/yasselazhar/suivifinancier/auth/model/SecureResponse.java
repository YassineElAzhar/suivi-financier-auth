package com.yasselazhar.suivifinancier.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * Created by Yassine EL-AZHAR
 */
@Entity
@Table(name = "secure_response")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"date_creation", "date_modification"},
        allowGetters = true)
public class SecureResponse {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;

    private int secureQuestionId;

    @NotBlank
    private String response;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date dateCreation;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date dateModification;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getSecureQuestionId() {
		return secureQuestionId;
	}

	public void setSecureQuestionId(int secureQuestionId) {
		this.secureQuestionId = secureQuestionId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	@Override
	public String toString() {
		return "SecureResponse [id=" + id + ", userId=" + userId + ", secureQuestionId=" + secureQuestionId
				+ ", response=" + response + ", dateCreation=" + dateCreation + ", dateModification=" + dateModification
				+ "]";
	}
    
}
