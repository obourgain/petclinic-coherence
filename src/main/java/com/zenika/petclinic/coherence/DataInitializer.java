package com.zenika.petclinic.coherence;

import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.samples.petclinic.Owner;
import org.springframework.samples.petclinic.Pet;
import org.springframework.samples.petclinic.PetType;
import org.springframework.samples.petclinic.Specialty;
import org.springframework.samples.petclinic.Vet;
import org.springframework.samples.petclinic.Visit;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

/**
 * @author olivier
 * 
 */
public class DataInitializer implements ServletContextListener {

	NamedCache petTypeCache = CacheFactory.getCache("pet-types-cache");
	NamedCache vetCache = CacheFactory.getCache("vet-cache");
	NamedCache ownerCache = CacheFactory.getCache("owner-cache");
	NamedCache visitCache = CacheFactory.getCache("visit-cache");

	@SuppressWarnings("deprecation")
	public void contextInitialized(ServletContextEvent sce) {

		vetCache.put(1, buildVet(1, "James", "Carter"));
		vetCache.put(2, buildVet(2, "Helen", "Leary", "radiology"));
		vetCache.put(3, buildVet(3, "Linda", "Douglas", "dentistry", "surgery"));
		vetCache.put(4, buildVet(4, "Rafael", "Ortega", "surgery"));
		vetCache.put(5, buildVet(5, "Henry", "Stevens", "radiology"));
		vetCache.put(6, buildVet(6, "Sharon", "Jenkins"));

		petTypeCache.put(1, buildPetType(1, "dog"));
		petTypeCache.put(2, buildPetType(2, "lizard"));
		petTypeCache.put(3, buildPetType(3, "snake"));
		petTypeCache.put(4, buildPetType(4, "bird"));
		petTypeCache.put(5, buildPetType(5, "hamster"));

		ownerCache.put(1, buildOwner(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023"));
		ownerCache.put(2, buildOwner(2, "Betty", "Davis", "638 Cardinal Ave.", "Sun Prairie", "6085551749"));
		ownerCache.put(3, buildOwner(3, "Eduardo", "Rodriquez", "2693 Commerce St.", "McFarland", "6085558763"));
		ownerCache.put(4, buildOwner(4, "Harold", "Davis", "563 Friendly St.", "Windsor", "6085553198"));
		ownerCache.put(5, buildOwner(5, "Peter", "McTavish", "2387 S. Fair Way", "Madison", "6085552765"));
		ownerCache.put(6, buildOwner(6, "Jean", "Coleman", "105 N. Lake St.", "Monona", "6085552654"));
		ownerCache.put(7, buildOwner(7, "Jeff", "Black", "1450 Oak Blvd.", "Monona", "6085555387"));
		ownerCache.put(8, buildOwner(8, "Maria", "Escobito", "345 Maple St.", "Madison", "6085557683"));
		ownerCache.put(9, buildOwner(9, "David", "Schroeder", "2749 Blackhawk Trail", "Madison", "6085559435"));
		ownerCache.put(10, buildOwner(10, "Carlos", "Estaban", "2335 Independence La.", "Waunakee", "6085555487"));

		buildAndAddPetToOwner(1, "Leo", new Date(100, 9, 7), (PetType) petTypeCache.get(1), 1);
		buildAndAddPetToOwner(2, "Basil", new Date(102, 8, 6), (PetType) petTypeCache.get(6), 2);
		buildAndAddPetToOwner(3, "Rosy", new Date(101, 4, 17), (PetType) petTypeCache.get(2), 3);
		buildAndAddPetToOwner(4, "Jewel", new Date(100, 3, 7), (PetType) petTypeCache.get(2), 3);
		buildAndAddPetToOwner(5, "Iggy", new Date(100, 11, 30), (PetType) petTypeCache.get(3), 4);
		buildAndAddPetToOwner(6, "George", new Date(100, 1, 20), (PetType) petTypeCache.get(4), 5);
		buildAndAddPetToOwner(7, "Samantha", new Date(95, 9, 4), (PetType) petTypeCache.get(1), 6);
		buildAndAddPetToOwner(8, "Max", new Date(95, 9, 4), (PetType) petTypeCache.get(1), 6);
		buildAndAddPetToOwner(9, "Lucky", new Date(99, 8, 4), (PetType) petTypeCache.get(5), 7);
		buildAndAddPetToOwner(10, "Mulligan", new Date(97, 2, 24), (PetType) petTypeCache.get(2), 8);
		buildAndAddPetToOwner(11, "Freddy", new Date(100, 3, 9), (PetType) petTypeCache.get(5), 9);
		buildAndAddPetToOwner(12, "Lucky", new Date(100, 6, 24), (PetType) petTypeCache.get(2), 10);
		buildAndAddPetToOwner(13, "Sly", new Date(102, 6, 8), (PetType) petTypeCache.get(1), 10);

		buildVisitAndAddToOwner(1, 7, new Date(96, 3, 4), "rabies shot");
		buildVisitAndAddToOwner(2, 8, new Date(96, 3, 4), "rabies shot");
		buildVisitAndAddToOwner(3, 8, new Date(96, 6, 4), "neutered");
		buildVisitAndAddToOwner(4, 7, new Date(96, 9, 4), "spayed");
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}

	private Vet buildVet(int id, String firstName, String lastName, String... specialties) {
		Vet vet = new Vet();
		vet.setId(id);
		vet.setFirstName(firstName);
		vet.setLastName(lastName);
		for (String specialtyName : specialties) {
			Specialty specialty = new Specialty();
			specialty.setName(specialtyName);
			vet.addSpecialty(specialty);
		}
		return vet;
	}

	private PetType buildPetType(int id, String petTypeName) {
		PetType petType = new PetType();
		petType.setId(id);
		petType.setName(petTypeName);
		return petType;
	}

	private Owner buildOwner(int id, String firstName, String lastName, String address, String city, String tel) {
		Owner owner = new Owner();
		owner.setId(id);
		owner.setFirstName(firstName);
		owner.setLastName(lastName);
		owner.setAddress(address);
		owner.setCity(city);
		owner.setTelephone(tel);

		return owner;
	}

	private void buildAndAddPetToOwner(int id, String name, Date birthDate, PetType petType, int ownerId) {
		Pet pet = new Pet();
		pet.setId(id);
		pet.setName(name);
		pet.setType(petType);
		pet.setBirthDate(birthDate);

		Owner owner = (Owner) ownerCache.get(ownerId);
		owner.addPet(pet);
		ownerCache.put(owner.getId(), owner);

	}

	@SuppressWarnings("unchecked")
	private void buildVisitAndAddToOwner(int id, int petId, Date date, String description) {
		Visit visit = new Visit();
		visit.setId(id);
		visit.setDate(date);
		visit.setDescription(description);

		for (Owner owner : (Collection<Owner>) ownerCache.values()) {
			for (Pet pet : owner.getPets()) {
				if (pet.getId() == petId) {
					visit.setPetId(pet.getId());
					visitCache.put(visit.getId(), visit);
					ownerCache.put(owner.getId(), owner);
					break;
				}
			}
		}
	}

}