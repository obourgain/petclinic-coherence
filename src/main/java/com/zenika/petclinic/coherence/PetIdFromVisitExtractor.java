package com.zenika.petclinic.coherence;

import org.springframework.samples.petclinic.Visit;

import com.tangosol.util.ValueExtractor;

/**
 * @author olivier
 *
 */
public class PetIdFromVisitExtractor implements ValueExtractor {

	public Object extract(Object o) {
		Visit visit = (Visit) o;
		return visit.getPetId();
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

}
