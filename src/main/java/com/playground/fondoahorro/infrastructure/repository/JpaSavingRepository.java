package com.playground.fondoahorro.infrastructure.repository;

import com.playground.fondoahorro.infrastructure.entities.SavingEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaSavingRepository extends JpaRepository<SavingEntity, Long> {

    List<SavingEntity> findByPerson_IdOrderBySavingDateDescIdDesc(long personId);

    @EntityGraph(attributePaths = "person")
    @Query("""
            select saving from SavingEntity saving
            where (:personId is null or saving.person.id = :personId)
            order by saving.savingDate desc, saving.id desc
            """)
    List<SavingEntity> search(@Param("personId") Long personId);

    @Query(value = """
            select coalesce(sum(amount_cents), 0)
            from savings
            where person_id = :personId and strftime('%Y', saving_date) = :year
            """, nativeQuery = true)
    Long totalForPersonAndYear(@Param("personId") long personId, @Param("year") String year);
}
