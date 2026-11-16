CREATE TABLE IF NOT EXISTS storages (
                                        id BIGSERIAL PRIMARY KEY,
                                        storage_type VARCHAR(20) NOT NULL,
    bucket VARCHAR(100) NOT NULL,
    path VARCHAR(200) NOT NULL,
    CONSTRAINT uq_storage_bucket_path UNIQUE (bucket, path)
    );

INSERT INTO storages (storage_type, bucket, path)
VALUES
    ('STAGING', 'staging-bucket', '/files'),
    ('PERMANENT', 'permanent-bucket', '/files')
    ON CONFLICT (bucket, path) DO NOTHING;