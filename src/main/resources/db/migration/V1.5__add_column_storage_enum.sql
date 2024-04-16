ALTER TABLE ticketings
    ADD COLUMN storage_type ENUM('LOCAL', 'S3', 'SEAWEEDFS'),
ADD COLUMN thumbnail_path VARCHAR(500);