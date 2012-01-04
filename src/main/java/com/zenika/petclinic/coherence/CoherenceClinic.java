package com.zenika.petclinic.coherence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
import com.tangosol.util.Filter;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.filter.ContainsFilter;
import com.tangosol.util.filter.EqualsFilter;
import com.tangosol.util.filter.LikeFilter;
import com.tangosol.util.filter.LimitFilter;

/**
 * @author Olivier Bourgain
 * @author Guillaume Tinon
 */
public class CoherenceClinic implements Clinic {

	public Collection<Vet> getVets() throws DataAccessException {
		return getVetsCache().values();
	}

	public Collection<PetType> getPetTypes() throws DataAccessException {
		return getPetTypesCache().values();
	}

	public Collection<Owner> findOwners(String lastName) throws DataAccessException {
		Filter lastNameFilter = new LikeFilter(new ReflectionExtractor("getLastName"), lastName + "%", '\\', true);
		Filter limitFilter = new LimitFilter(lastNameFilter, 30);
		Set<Entry<Integer, Owner>> entrySet = getOwnersCache().entrySet(limitFilter);
		List<Owner> result = new ArrayList<Owner>();
		for (Entry<Integer, Owner> entry : entrySet) {
			result.add(entry.getValue());
		}
		return result;
	}

	public Owner loadOwner(int id) throws DataAccessException {
		return (Owner) getOwnersCache().get(id);
	}

	public Pet loadPet(int id) throws DataAccessException {
		ContainsFilter filter = new ContainsFilter(new PetIdsExtractor(), id);

		// there should be at most one owner matching the filter
		Set<Entry<Integer, Owner>> entries = getOwnersCache().entrySet(filter);

		if (entries.isEmpty()) {
			return null;
		}

		Entry<Integer, Owner> entry = entries.iterator().next();
		Owner owner = entry.getValue();
		for (Pet pet : owner.getPets()) {
			if (pet.getId().equals(id)) {
				return pet;
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
		if (visit.getId() == null) {
			visit.setId(RandomUtils.nextInt());
		}

		getVisitsCache().put(visit.getId(), visit);
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

	private NamedCache getVisitsCache() {
		return CacheFactory.getCache("visit-cache");
	}

	public Set<Visit> loadVisitsForPet(int petId) throws DataAccessException {
		ValueExtractor extractor = new PetIdFromVisitExtractor();
		Filter filter = new EqualsFilter(extractor, petId);

		// no method values(filter), so we have to iterate on entry.getValue() to build the result :/
		Set<Entry<Integer, Visit>> visits = getVisitsCache().entrySet(filter);
		Set<Visit> result = new HashSet<Visit>();
		for (Entry<Integer, Visit> entry : visits) {
			result.add(entry.getValue());
		}
		return result;
	}
}
