package com.zenika.petclinic.coherence;

import org.springframework.samples.petclinic.Specialty;
import org.springframework.samples.petclinic.Vet;

import com.tangosol.util.InvocableMap;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * @author Olivier Bourgain
 */
public class AddSpecialtyToVetsProcessor extends AbstractProcessor {

	private Specialty specialty;

	public AddSpecialtyToVetsProcessor(Specialty specialty) {
		this.specialty = specialty;
	}

	public Object process(InvocableMap.Entry entry) {
		Vet vet = (Vet) entry.getValue();
		if (vet.getSpecialties().contains(specialty)) {
			return false;
		}
		vet.addSpecialty(specialty);
		return true;
	}
}
