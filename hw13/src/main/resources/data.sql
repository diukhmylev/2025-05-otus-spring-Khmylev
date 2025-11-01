INSERT INTO "authors" ("id", "full_name") VALUES
  ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Author 1'),
  ('c9bf9e57-1685-4c89-bafb-ff5af830be8a', 'Author 2');

INSERT INTO "genres" ("id", "name") VALUES
  ('1e1f0d7e-6b80-4b43-a9f5-f9348bfcf31c', 'Genre 1'),
  ('2e3c4a8b-90ad-4b23-9211-0ad68230f684', 'Genre 2'),
  ('3a5c6d7e-7e3e-41a0-b943-7e687c0b622c', 'Genre 3');

INSERT INTO "books" ("id", "title", "author_id") VALUES
  ('4c3c7b7a-3f44-4e31-8967-0612d119a1d0', 'Book 1', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
  ('5e4a8b9c-6a11-4a12-982f-8d084e981b44', 'Book 2', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
  ('6d2e9f3b-81e5-4a2b-b1ec-2df0915c042f', 'Book 3', 'c9bf9e57-1685-4c89-bafb-ff5af830be8a');

INSERT INTO "comments" ("id", "text", "book_id") VALUES
  ('7a3b2e1c-9e1f-4f22-a55b-1f3a786f3a60', 'Comment 1 for Book 1', '4c3c7b7a-3f44-4e31-8967-0612d119a1d0'),
  ('8b4e9c3a-2e1a-4fa8-bd42-02e9d0212a89', 'Comment 2 for Book 1', '4c3c7b7a-3f44-4e31-8967-0612d119a1d0'),
  ('9c5a1d2f-732b-4a4e-aeb0-0dfe9f5b65ff', 'Comment 1 for Book 2', '5e4a8b9c-6a11-4a12-982f-8d084e981b44');

INSERT INTO "book_genres" ("book_id", "genre_id") VALUES
  ('4c3c7b7a-3f44-4e31-8967-0612d119a1d0', '1e1f0d7e-6b80-4b43-a9f5-f9348bfcf31c'),
  ('4c3c7b7a-3f44-4e31-8967-0612d119a1d0', '2e3c4a8b-90ad-4b23-9211-0ad68230f684'),
  ('5e4a8b9c-6a11-4a12-982f-8d084e981b44', '3a5c6d7e-7e3e-41a0-b943-7e687c0b622c'),
  ('6d2e9f3b-81e5-4a2b-b1ec-2df0915c042f', '2e3c4a8b-90ad-4b23-9211-0ad68230f684');

INSERT INTO "users" ("id", "username", "password", "role") VALUES
('aa3fe87f-b2fd-4ad2-bf8c-e7688fdc1d43', 'admin', '{bcrypt}$2a$12$goTLqAa/9nSkxPHldMRj3O2dDydEE3B2qp2ESDM4/mLwQGCBPVF1K', 'ROLE_ADMIN'),
('bb6de32e-0e4e-4e9a-82c0-c3ebd3d3a184', 'user', '{bcrypt}$2a$12$goTLqAa/9nSkxPHldMRj3O2dDydEE3B2qp2ESDM4/mLwQGCBPVF1K', 'ROLE_USER');
