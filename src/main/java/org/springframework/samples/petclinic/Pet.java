package org.springframework.samples.petclinic;

import java.util.Date;

/**
 * Simple JavaBean business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public class Pet extends NamedEntity {

	private Date birthDate;

	private PetType type;

	private Owner owner;

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getBirthDate() {
		return this.birthDate;
	}

	public void setType(PetType type) {
		this.type = type;
	}

	public PetType getType() {
		return this.type;
	}

	protected void setOwner(Owner owner) {
		this.owner = owner;
	}

	public Owner getOwner() {
		return this.owner;
	}

}
