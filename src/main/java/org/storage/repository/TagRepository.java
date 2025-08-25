package org.storage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.storage.repository.entity.MetadataEntity;
import org.storage.repository.entity.TagEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, UUID> {

    List<TagEntity> findByTag(String tag);

    @Query("SELECT t.metadata FROM TagEntity t WHERE t.tag = :tag")
    Page<MetadataEntity> findFilesByTag(@Param("tag") String tag, Pageable pageable);
}