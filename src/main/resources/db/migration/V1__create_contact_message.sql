CREATE TABLE contact_message (
  id UUID PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(254) NOT NULL,
  subject VARCHAR(160) NOT NULL,
  message VARCHAR(2000) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  ip_hash VARCHAR(64),
  notified BOOLEAN NOT NULL DEFAULT FALSE,
  status VARCHAR(10) NOT NULL DEFAULT 'NEW'
);

CREATE INDEX idx_contact_message_created_at ON contact_message (created_at);
