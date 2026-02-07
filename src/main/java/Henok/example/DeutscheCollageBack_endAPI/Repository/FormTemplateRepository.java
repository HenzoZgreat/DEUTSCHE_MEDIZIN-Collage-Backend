package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.FormTemplate;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

// FormTemplateRepository
// Repository interface for FormTemplate entity operations.
// Extends JpaRepository for standard CRUD methods.
// Custom methods can be added if needed, but for now, we use existsByName for uniqueness check.
public interface FormTemplateRepository extends JpaRepository<FormTemplate, Long> {

    // Checks if a form template with the given name already exists.
    // Why: Enforces unique names to prevent duplicates.
    boolean existsByName(String name);

    /**
     * Finds all form templates where at least one of the provided roles is allowed.
     * Used for role-based filtering.
     */
    List<FormTemplate> findByForRolesIn(Collection<Role> roles);
}