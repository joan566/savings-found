package com.playground.fondoahorro.infrastructure.repository;

import com.playground.fondoahorro.infrastructure.entities.AppSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAppSettingRepository extends JpaRepository<AppSettingEntity, String> {
}
