package org.storage.repository.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "file_tags")
public class TagEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "tag", nullable = false)
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metadata_id", nullable = false)
    private MetadataEntity metadata;

    public TagEntity() {}

    public TagEntity(String tag, MetadataEntity metadata) {
        this.tag = tag;
        this.metadata = metadata;
    }

    public String getTag() {
        return tag;
    }
}

