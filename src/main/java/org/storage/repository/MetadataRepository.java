package org.storage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.storage.model.Visibility;
import org.storage.repository.entity.MetadataEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataRepository extends JpaRepository<MetadataEntity, UUID> {

    Optional<MetadataEntity> findByIdAndDeletedIsFalse(UUID id);

    Page<MetadataEntity> findByUserIdAndDeletedIsFalseOrVisibilityAndDeletedIsFalse(UUID userId, Visibility visibility, Pageable pageable);

    Optional<MetadataEntity> findByContentHashAndUserIdAndDeletedIsFalse(String contentHash, UUID userId);

    Optional<MetadataEntity> findByFileNameAndUserIdAndDeletedIsFalse(String fileName, UUID userId);
}
