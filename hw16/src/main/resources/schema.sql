CREATE TABLE "authors" (
    "id" UUID PRIMARY KEY,
    "full_name" VARCHAR(255) NOT NULL
);

CREATE TABLE "genres" (
    "id" UUID PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL
);

CREATE TABLE "books" (
    "id" UUID PRIMARY KEY,
    "title" VARCHAR(255) NOT NULL,
    "author_id" UUID NOT NULL,
    CONSTRAINT fk_books_author FOREIGN KEY ("author_id") REFERENCES "authors" ("id")
);

CREATE TABLE "comments" (
    "id" UUID PRIMARY KEY,
    "text" VARCHAR(1000) NOT NULL,
    "book_id" UUID NOT NULL,
    CONSTRAINT fk_comments_book FOREIGN KEY ("book_id") REFERENCES "books" ("id") ON DELETE CASCADE
);

CREATE TABLE "book_genres" (
    "book_id" UUID NOT NULL,
    "genre_id" UUID NOT NULL,
    PRIMARY KEY ("book_id", "genre_id"),
    CONSTRAINT fk_book FOREIGN KEY ("book_id") REFERENCES "books" ("id"),
    CONSTRAINT fk_genre FOREIGN KEY ("genre_id") REFERENCES "genres" ("id")
);

CREATE TABLE "users" (
    "id" UUID PRIMARY KEY,
    "username" VARCHAR(255) NOT NULL UNIQUE,
    "password" VARCHAR(255) NOT NULL,
    "role" VARCHAR(255)
);
