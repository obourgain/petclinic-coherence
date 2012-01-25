package com.zenika.petclinic.coherence;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.samples.petclinic.Owner;
import org.springframework.samples.petclinic.Pet;

import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * @author olivier
 */
public class StorePetProcessor extends AbstractProcessor {

	private Pet pet;
	
	public StorePetProcessor(Pet pet) {
		this.pet = pet;
	}

	public Object process(Entry entry) {
		Owner owner = (Owner) entry.getValue();
		
		if (pet.getId() == null) {
			pet.setId(RandomUtils.nextInt());
			owner.addPet(pet);
		} else {
			Pet existingPet = owner.getPet(pet.getName());
			existingPet.setBirthDate(pet.getBirthDate());
			existingPet.setName(pet.getName());
			existingPet.setType(pet.getType());
		}
		entry.setValue(owner);

		return null;
	}
}
