package com.playground.fondoahorro.infrastructure.repository;

import com.playground.fondoahorro.infrastructure.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaPersonRepository extends JpaRepository<PersonEntity, Long> {

    List<PersonEntity> findAllByOrderByNameAsc();

    List<PersonEntity> findByActiveTrueOrderByNameAsc();

    List<PersonEntity> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<PersonEntity> findByNameContainingIgnoreCaseAndActiveTrueOrderByNameAsc(String name);

    @Query(value = SUMMARY_SELECT + """
            where (:query is null or p.name like '%' || :query || '%' collate nocase)
              and (:includeInactive = 1 or p.active = 1)
            order by p.name
            """, nativeQuery = true)
    List<PersonSummaryProjection> findAllWithSummary(
            @Param("query") String query, @Param("includeInactive") int includeInactive);

    @Query(value = SUMMARY_SELECT + " where p.id = :id", nativeQuery = true)
    Optional<PersonSummaryProjection> findSummaryById(@Param("id") long id);

    String SUMMARY_SELECT = """
            select p.id as id, p.name as name, p.birth_date as birthDate, p.phone as phone,
                   p.active as active, p.created_at as createdAt, p.updated_at as updatedAt,
                   coalesce((select sum(s.amount_cents) from savings s where s.person_id = p.id), 0) as totalSavingsCents,
                   coalesce((select sum(l.principal_balance_cents + l.interest_owed_cents)
                             from loans l where l.person_id = p.id and l.status = 'ACTIVE'), 0) as outstandingDebtCents
            from persons p
            """;

    interface PersonSummaryProjection {
        Long getId();
        String getName();
        String getBirthDate();
        String getPhone();
        Integer getActive();
        String getCreatedAt();
        String getUpdatedAt();
        Long getTotalSavingsCents();
        Long getOutstandingDebtCents();
    }
}
