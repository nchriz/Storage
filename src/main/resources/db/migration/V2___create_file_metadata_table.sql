CREATE
EXTENSION IF NOT EXISTS "pgcrypto";
CREATE
EXTENSION IF NOT EXISTS citext;


CREATE TABLE metadata
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    file_name    VARCHAR(255) NOT NULL,
    file_size    BIGINT       NOT NULL,
    file_path    VARCHAR(255) NOT NULL,
    user_id      UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    visibility   VARCHAR(20) NOT NULL,
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    uploaded_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content_hash VARCHAR(64),
    content_type VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX idx_user_content_hash ON metadata (user_id, content_hash) WHERE deleted = FALSE;

CREATE TABLE file_tags
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    metadata_id UUID   NOT NULL REFERENCES metadata (id) ON DELETE CASCADE,
    tag         CITEXT NOT NULL,
    UNIQUE (metadata_id, tag)
);
