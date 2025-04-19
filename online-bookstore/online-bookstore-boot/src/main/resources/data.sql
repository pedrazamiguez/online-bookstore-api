INSERT INTO book_types (code, name, description) VALUES
('NEW_RELEASE',     'New Release',  'Recently released book'),
('REGULAR',         'Regular',      'Standard book'),
('OLD_EDITION',     'Old Edition',  'Older or outdated edition');

INSERT INTO books (author, created_at, created_by, genre, isbn, price, publisher, title, type_code, updated_at, updated_by, year_published, id) VALUES
('George Orwell',    NOW(), 'admin', 'FICTION',   '9780451524935', 12.99, 'Secker & Warburg', '1984',                    'OLD_EDITION', NOW(), 'admin', 1949, next value for books_seq),
('J.K. Rowling',     NOW(), 'admin', 'FANTASY',   '9780747542988', 24.50, 'Bloomsbury',       'Harry Potter 1',          'NEW_RELEASE', NOW(), 'admin', 1997, next value for books_seq),
('J.R.R. Tolkien',   NOW(), 'admin', 'FANTASY',   '9780261103573', 29.99, 'Allen & Unwin',     'The Hobbit',             'REGULAR', NOW(), 'admin', 1937, next value for books_seq),
('Harper Lee',       NOW(), 'admin', 'FICTION',   '9780061120084', 14.99, 'J.B. Lippincott',   'To Kill a Mockingbird',  'OLD_EDITION', NOW(), 'admin', 1960, next value for books_seq),
('Michelle Obama',   NOW(), 'admin', 'BIOGRAPHY', '9781524763138', 19.95, 'Crown Publishing',  'Becoming',               'NEW_RELEASE', NOW(), 'admin', 2018, next value for books_seq),
('Stephen King',     NOW(), 'admin', 'THRILLER',  '9780307743657', 16.80, 'Anchor Books',      'It',                     'REGULAR', NOW(), 'admin', 1986, next value for books_seq),
('Dan Brown',        NOW(), 'admin', 'THRILLER',  '9780307474278', 15.95, 'Doubleday',         'The Lost Symbol',        'REGULAR', NOW(), 'admin', 2009, next value for books_seq),
('Yuval Harari',     NOW(), 'admin', 'HISTORY',   '9780062316097', 21.00, 'Harper',            'Sapiens',                'NEW_RELEASE', NOW(), 'admin', 2011, next value for books_seq),
('Ernest Hemingway', NOW(), 'admin', 'FICTION',   '9780684801223', 13.40, 'Charles Scribner',  'The Old Man and the Sea','OLD_EDITION', NOW(), 'admin', 1952, next value for books_seq),
('Delia Owens',      NOW(), 'admin', 'FICTION',   '9780735219106', 17.60, 'G.P. Putnam',       'Where the Crawdads Sing','NEW_RELEASE', NOW(), 'admin', 2018, next value for books_seq);

-- 12 copies for 9780451524935
INSERT INTO book_copies (book_id, created_at, created_by, updated_at, updated_by, id)
SELECT b.id, NOW(), 'admin', NOW(), 'admin', next value for book_copies_seq
FROM books b, system_range(1, 12)
WHERE b.isbn = '9780451524935';

-- 33 copies for 9780747542988
INSERT INTO book_copies (book_id, created_at, created_by, updated_at, updated_by, id)
SELECT b.id, NOW(), 'admin', NOW(), 'admin', next value for book_copies_seq
FROM books b, system_range(1, 33)
WHERE b.isbn = '9780747542988';

-- 45 copies for 9780261103573
INSERT INTO book_copies (book_id, created_at, created_by, updated_at, updated_by, id)
SELECT b.id, NOW(), 'admin', NOW(), 'admin', next value for book_copies_seq
FROM books b, system_range(1, 45)
WHERE b.isbn = '9780261103573';

-- 62 copies for 9780062316097
INSERT INTO book_copies (book_id, created_at, created_by, updated_at, updated_by, id)
SELECT b.id, NOW(), 'admin', NOW(), 'admin', next value for book_copies_seq
FROM books b, system_range(1, 62)
WHERE b.isbn = '9780062316097';
