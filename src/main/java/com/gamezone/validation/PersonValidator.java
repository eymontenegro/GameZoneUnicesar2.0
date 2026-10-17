/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Person;

import java.util.List;

/**
 * Static validation rules for the person module (clients and sellers).
 *
 * <p>The uniqueness check receives the existing list as a parameter instead of
 * querying a repository, so this class stays independent from persistence.</p>
 */
public final class PersonValidator {

    private PersonValidator() {
        // Utility class: no instances.
    }

    /**
     * Validates the basic data of a person.
     *
     * @param id    person identifier, must not be null or blank
     * @param name  person name, must not be null or blank
     * @param email person email, must not be blank and must contain an at sign and a dot
     * @param phone person phone; it is optional, so it is not validated
     * @throws InvalidDataException if any value breaks its rule
     */
    public static void validatePersonData(String id, String name, String email, String phone) {
        requireText(id, "identificador");
        requireText(name, "nombre");
        requireText(email, "correo electrónico");

        if (!email.contains("@") || !email.contains(".")) {
            throw new InvalidDataException("correo electrónico",
                    "debe tener un formato válido (por ejemplo, usuario@dominio.com)");
        }
    }

    /**
     * Validates that a person was found.
     *
     * @param person   person returned by a search, may be null
     * @param personId identifier that was searched
     * @throws ResourceNotFoundException if the person is null
     */
    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("persona", personId);
        }
    }

    /**
     * Validates that no existing person uses the given identifier.
     * The comparison ignores letter case, like the original service did.
     *
     * @param existing people already registered (a list of Person or of any subclass)
     * @param newId    identifier of the person about to be registered
     * @throws BusinessRuleException if the identifier is already in use
     */
    public static void validateUniquePerson(List<? extends Person> existing, String newId) {
        if (existing == null || newId == null) {
            return;
        }
        for (Person person : existing) {
            if (person != null && newId.equalsIgnoreCase(person.getId())) {
                throw new BusinessRuleException(
                        "Ya existe una persona registrada con el identificador '" + newId + "'");
            }
        }
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(fieldName, "no puede ser nulo ni vacío");
        }
    }
}
