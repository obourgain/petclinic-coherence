package com.zenika.petclinic.coherence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Owner;
import org.springframework.samples.petclinic.Pet;
import org.springframework.samples.petclinic.PetType;
import org.springframework.samples.petclinic.Vet;
import org.springframework.samples.petclinic.Visit;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

/**
 * @author olivier
 *
 */
public class CoherenceClinic implements Clinic {

	public Collection<Vet> getVets() throws DataAccessException {
		return getVetsCache().values();
	}

	public Collection<PetType> getPetTypes() throws DataAccessException {
		return getPetTypesCache().values();
	}

	public Collection<Owner> findOwners(String lastName) throws DataAccessException {
		Collection<Owner> owners = getOwnersCache().values();
		String lowerCasedLastName = lastName.toLowerCase();

		List<Owner> result = new ArrayList<Owner>();
		for (Owner owner : owners) {
			if (owner.getLastName().toLowerCase().startsWith(lowerCasedLastName)) {
				result.add(owner);
			}
		}
		return result;
	}

	public Owner loadOwner(int id) throws DataAccessException {
		return (Owner) getOwnersCache().get(id);
	}

	public Pet loadPet(int id) throws DataAccessException {
		Collection<Owner> owners = getOwnersCache().values();
		for (Owner owner : owners) {
			for (Pet pet : owner.getPets()) {
				if (pet.getId().equals(id)) {
					return pet;
				}
			}
		}
		return null;
	}

	public void storeOwner(Owner owner) throws DataAccessException {
		if (owner.getId() == null) {
			owner.setId(RandomUtils.nextInt());
		}
		getOwnersCache().put(owner.getId(), owner);
	}

	// FIXME not transactional !!!
	public void storePet(Pet pet) throws DataAccessException {
		Owner owner = (Owner) getOwnersCache().get(pet.getOwner().getId());

		if (owner == null) {
			throw new IllegalStateException("Owner with id: " + pet.getOwner().getId() + " not found");
		}

		if (pet.getId() == null) {
			pet.setId(RandomUtils.nextInt());
			owner.addPet(pet);
		} else {
			Pet existingPet = owner.getPet(pet.getName());
			existingPet.setBirthDate(pet.getBirthDate());
			existingPet.setName(pet.getName());
			existingPet.setType(pet.getType());
		}
		getOwnersCache().put(owner.getId(), owner);
	}

	public void storeVisit(Visit visit) throws DataAccessException {
		for (Owner owner : (Collection<Owner>) getOwnersCache().values()) {
			for (Pet pet : owner.getPets()) {
				if (pet.getId().equals(visit.getPet().getId())) {
					if (visit.getId() == null) {
						visit.setId(RandomUtils.nextInt());
					}
					pet.addVisit(visit);
					getOwnersCache().put(owner.getId(), owner);
					break;
				}
			}
		}
	}

	public void deletePet(int id) throws DataAccessException {
		throw new UnsupportedOperationException("Pet with id " + id + " is not allowed to die");
	}

	private NamedCache getOwnersCache() {
		return CacheFactory.getCache("owner-cache");
	}

	private NamedCache getPetTypesCache() {
		return CacheFactory.getCache("pet-types-cache");
	}

	private NamedCache getVetsCache() {
		return CacheFactory.getCache("vet-cache");
	}
}
