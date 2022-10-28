package com.yasselazha.suivifinancier.auth.model;

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
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"date_creation", "date_modification"},
        allowGetters = true)
public class User {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotBlank
    private String email;

    @NotBlank
    private String dateNaissance;

    @NotBlank
    private String adresse;

    @NotBlank
    private String ville;

    @NotBlank
    private String zip;

    private int typeProfil;

    private int actif;
    
    private int password;


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

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(String dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public int getTypeProfil() {
		return typeProfil;
	}

	public void setTypeProfil(int typeProfil) {
		this.typeProfil = typeProfil;
	}

	public int getActif() {
		return actif;
	}

	public void setActif(int actif) {
		this.actif = actif;
	}

	public int getPassword() {
		return password;
	}

	public void setPassword(int password) {
		this.password = password;
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
		return "User [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", email=" + email
				+ ", dateNaissance=" + dateNaissance + ", adresse=" + adresse + ", ville=" + ville + ", zip=" + zip
				+ ", typeProfil=" + typeProfil + ", actif=" + actif + ", password=" + password + ", dateCreation=" + dateCreation
				+ ", dateModification=" + dateModification + "]";
	}
    
    

}
