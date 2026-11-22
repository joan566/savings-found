package com.playground.fondoahorro.infrastructure.adapters;

import com.playground.fondoahorro.domain.outputport.AppSettingsRepository;
import com.playground.fondoahorro.infrastructure.entities.AppSettingEntity;
import com.playground.fondoahorro.infrastructure.repository.JpaAppSettingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class AppSettingsRepositoryAdapter implements AppSettingsRepository {

    private final JpaAppSettingRepository repository;

    public AppSettingsRepositoryAdapter(JpaAppSettingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<String> get(String key) {
        return repository.findById(key).map(AppSettingEntity::getValue);
    }

    @Override
    public void set(String key, String value) {
        AppSettingEntity entity = repository.findById(key).orElseGet(AppSettingEntity::new);
        entity.setKey(key);
        entity.setValue(value);
        entity.setUpdatedAt(LocalDateTime.now().withNano(0).toString());
        repository.save(entity);
    }
}
