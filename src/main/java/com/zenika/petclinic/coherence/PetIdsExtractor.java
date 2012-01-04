package com.zenika.petclinic.coherence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.samples.petclinic.Owner;
import org.springframework.samples.petclinic.Pet;

import com.tangosol.util.ValueExtractor;

/**
 * @author Olivier Bourgain
 * @author Guillaume Tinon
 */
public class PetIdsExtractor implements ValueExtractor {

	public Object extract(Object o) {
		Owner owner = (Owner) o;

		List<Integer> extractedPetIds = new ArrayList<Integer>();

		for (Pet pet : owner.getPets()) {
			extractedPetIds.add(pet.getId());
		}
		return extractedPetIds;
	}

}
