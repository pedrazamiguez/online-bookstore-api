INSERT INTO book_types (code, name, description) VALUES
('NEW_RELEASE',     'New Release',  'Recently released book'),
('REGULAR',         'Regular',      'Standard book'),
('OLD_EDITION',     'Old Edition',  'Older or outdated edition');

INSERT INTO books (author, created_at, created_by, genre, isbn, price, publisher, title, type_code, updated_at, updated_by, year_published, id) VALUES
('George Orwell',    NOW(), 'superadmin', 'FICTION',   '9780451524935', 12.99, 'Secker & Warburg', '1984',                    'OLD_EDITION', NOW(), 'superadmin', 1949, next value for books_seq),
('J.K. Rowling',     NOW(), 'superadmin', 'FANTASY',   '9780747542988', 24.50, 'Bloomsbury',       'Harry Potter 1',          'NEW_RELEASE', NOW(), 'superadmin', 1997, next value for books_seq),
('J.R.R. Tolkien',   NOW(), 'superadmin', 'FANTASY',   '9780261103573', 29.99, 'Allen & Unwin',     'The Hobbit',             'REGULAR', NOW(), 'superadmin', 1937, next value for books_seq),
('Harper Lee',       NOW(), 'superadmin', 'FICTION',   '9780061120084', 14.99, 'J.B. Lippincott',   'To Kill a Mockingbird',  'OLD_EDITION', NOW(), 'superadmin', 1960, next value for books_seq),
('Michelle Obama',   NOW(), 'superadmin', 'BIOGRAPHY', '9781524763138', 19.95, 'Crown Publishing',  'Becoming',               'NEW_RELEASE', NOW(), 'superadmin', 2018, next value for books_seq),
('Stephen King',     NOW(), 'superadmin', 'THRILLER',  '9780307743657', 16.80, 'Anchor Books',      'It',                     'REGULAR', NOW(), 'superadmin', 1986, next value for books_seq),
('Dan Brown',        NOW(), 'superadmin', 'THRILLER',  '9780307474278', 15.95, 'Doubleday',         'The Lost Symbol',        'REGULAR', NOW(), 'superadmin', 2009, next value for books_seq),
('Yuval Harari',     NOW(), 'superadmin', 'HISTORY',   '9780062316097', 21.00, 'Harper',            'Sapiens',                'NEW_RELEASE', NOW(), 'superadmin', 2011, next value for books_seq),
('Ernest Hemingway', NOW(), 'superadmin', 'FICTION',   '9780684801223', 13.40, 'Charles Scribner',  'The Old Man and the Sea','OLD_EDITION', NOW(), 'superadmin', 1952, next value for books_seq),
('Delia Owens',      NOW(), 'superadmin', 'FICTION',   '9780735219106', 17.60, 'G.P. Putnam',       'Where the Crawdads Sing','NEW_RELEASE', NOW(), 'superadmin', 2018, next value for books_seq);

-- 12 copies for 9780451524935
INSERT INTO book_copies (book_id, created_at, created_by, updated_at, updated_by, id)
SELECT b.id, NOW(), 'superadmin', NOW(), 'superadmin', next value for book_copies_seq
FROM books b, system_range(1, 12)
WHERE b.isbn = '9780451524935';

-- 33 copies for 9780747542988
INSERT INTO book_copies (book_id, created_at, created_by, updated_at, updated_by, id)
SELECT b.id, NOW(), 'superadmin', NOW(), 'superadmin', next value for book_copies_seq
FROM books b, system_range(1, 33)
WHERE b.isbn = '9780747542988';

-- 45 copies for 9780261103573
INSERT INTO book_copies (book_id, created_at, created_by, updated_at, updated_by, id)
SELECT b.id, NOW(), 'superadmin', NOW(), 'superadmin', next value for book_copies_seq
FROM books b, system_range(1, 45)
WHERE b.isbn = '9780261103573';

-- 62 copies for 9780062316097
INSERT INTO book_copies (book_id, created_at, created_by, updated_at, updated_by, id)
SELECT b.id, NOW(), 'superadmin', NOW(), 'superadmin', next value for book_copies_seq
FROM books b, system_range(1, 62)
WHERE b.isbn = '9780062316097';

INSERT INTO customers (
  id, created_at, updated_at, created_by, updated_by, username, email, phone, address
) VALUES (
  next value for customers_seq, NOW(), NOW(),
  'superadmin', 'superadmin', 'bob', 'bob@example.com', '555-0101', '123 Main St'
);

INSERT INTO customers (
  id, created_at, updated_at, created_by, updated_by, username, email, phone, address
) VALUES (
  next value for customers_seq, NOW(), NOW(),
  'superadmin', 'superadmin', 'alice', 'alice@example.com', '555-0102', '456 Elm St'
);

INSERT INTO customers (
  id, created_at, updated_at, created_by, updated_by, username, email, phone, address
) VALUES (
  next value for customers_seq, NOW(), NOW(),
  'superadmin', 'superadmin', 'admin', 'admin@example.com', '555-0103', '789 Oak St'
);
INSERT INTO customers (
  id, created_at, updated_at, created_by, updated_by, username, email, phone, address
) VALUES (
  next value for customers_seq, NOW(), NOW(),
  'system', 'system', 'superadmin', 'superadmin@example.com', '555-0104', '321 Pine St'
);