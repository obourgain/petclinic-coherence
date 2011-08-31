package com.zenika.petclinic.coherence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	private Clinic jdbcClinic;

	public void setJdbcClinic(Clinic jdbcClinic) {
		this.jdbcClinic = jdbcClinic;
	}
	public Collection<Vet> getVets() throws DataAccessException {
		return jdbcClinic.getVets();
	}

	public Collection<PetType> getPetTypes() throws DataAccessException {
		return jdbcClinic.getPetTypes();
	}

	public Collection<Owner> findOwners(String lastName) throws DataAccessException {
		// load from the database and cache all matching results
		Collection<Owner> owners = jdbcClinic.findOwners(lastName);

		// inserts are batched to improve performance
		Map<Integer, Owner> ownersMap = new HashMap<Integer, Owner>();
		for (Owner owner : owners) {
			ownersMap.put(owner.getId(), owner);
		}
		getOwnersCache().putAll(ownersMap);

		return owners;
	}

	public Owner loadOwner(int id) throws DataAccessException {
		// search in the cache by key
		Owner owner = (Owner) getOwnersCache().get(id);
		if (owner == null) {
			// if not in cache, try to load from the database
			owner = jdbcClinic.loadOwner(id);
			if (owner != null) {
				// cache it
				getOwnersCache().put(id, owner);
			}
		}
		return owner;
	}

	public Pet loadPet(int id) throws DataAccessException {
		return jdbcClinic.loadPet(id);
	}

	public void storeOwner(Owner owner) throws DataAccessException {
		// store the owner in database to maintain consistency as we don't yet cache everything
		// we also still rely on this call to generate the owner's id
		jdbcClinic.storeOwner(owner);

		// put the owner in the cache
		getOwnersCache().put(owner.getId(), owner);
	}

	public void storePet(Pet pet) throws DataAccessException {
		jdbcClinic.storePet(pet);
		getOwnersCache().remove(pet.getOwner().getId());
	}

	public void storeVisit(Visit visit) throws DataAccessException {
		jdbcClinic.storeVisit(visit);
		getOwnersCache().remove(visit.getPet().getOwner().getId());
	}

	public void deletePet(int id) throws DataAccessException {
		jdbcClinic.deletePet(id);
	}

	private NamedCache getOwnersCache() {
		return CacheFactory.getCache("example-distributed");
	}
}
