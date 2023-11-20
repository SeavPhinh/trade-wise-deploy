CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
INSERT INTO categories (id, name)
VALUES (uuid_generate_v4(), 'VEHICLE'),
       (uuid_generate_v4(), 'PHONE & TABLETS'),
       (uuid_generate_v4(), 'COMPUTER & ACCESSORIES'),
       (uuid_generate_v4(), 'ELECTRONICS & APPLIANCES'),
       (uuid_generate_v4(), 'FASHION & BEAUTY'),
       (uuid_generate_v4(), 'FURNITURE & DECOR'),
       (uuid_generate_v4(), 'BOOKS, SPORT & HOBBIES'),
       (uuid_generate_v4(), 'HOME AND LANDS'),
       (uuid_generate_v4(), 'PETS');

-- Use the generated category UUIDs to insert sub-categories
INSERT INTO sub_categories (id, name, category_id)
VALUES
    (uuid_generate_v4(), 'CAR', (SELECT id FROM categories WHERE name = 'VEHICLE')),
    (uuid_generate_v4(), 'BYCYCLE', (SELECT id FROM categories WHERE name = 'VEHICLE')),
    (uuid_generate_v4(), 'MOTORCYCLES', (SELECT id FROM categories WHERE name = 'VEHICLE')),
    (uuid_generate_v4(), 'LORRIES, VANS & TRACTORS', (SELECT id FROM categories WHERE name = 'VEHICLE')),

    (uuid_generate_v4(), 'PHONES', (SELECT id FROM categories WHERE name = 'PHONE & TABLETS')),
    (uuid_generate_v4(), 'TABLETS', (SELECT id FROM categories WHERE name = 'PHONE & TABLETS')),
    (uuid_generate_v4(), 'SMART WATCHES', (SELECT id FROM categories WHERE name = 'PHONE & TABLETS')),
    (uuid_generate_v4(), 'ACCESSORIES', (SELECT id FROM categories WHERE name = 'PHONE & TABLETS')),

    (uuid_generate_v4(), 'LAPTOP', (SELECT id FROM categories WHERE name = 'COMPUTER & ACCESSORIES')),
    (uuid_generate_v4(), 'DESKTOP', (SELECT id FROM categories WHERE name = 'COMPUTER & ACCESSORIES')),
    (uuid_generate_v4(), 'MONITORS', (SELECT id FROM categories WHERE name = 'COMPUTER & ACCESSORIES')),
    (uuid_generate_v4(), 'PRINTER & SCANNERS', (SELECT id FROM categories WHERE name = 'COMPUTER & ACCESSORIES')),
    (uuid_generate_v4(), 'PARTS & ACCESSORIES SOFTWARE', (SELECT id FROM categories WHERE name = 'COMPUTER & ACCESSORIES')),

    (uuid_generate_v4(), 'WASHING MACHINES & DRYERS', (SELECT id FROM categories WHERE name = 'ELECTRONICS & APPLIANCES')),
    (uuid_generate_v4(), 'FRIDGE & FREEZERS', (SELECT id FROM categories WHERE name = 'ELECTRONICS & APPLIANCES')),
    (uuid_generate_v4(), 'SECURITY CAMERA', (SELECT id FROM categories WHERE name = 'ELECTRONICS & APPLIANCES')),
    (uuid_generate_v4(), 'TVS, VIDEO & AUDIO', (SELECT id FROM categories WHERE name = 'ELECTRONICS & APPLIANCES')),

    (uuid_generate_v4(), 'WOMEN`S FASHION', (SELECT id FROM categories WHERE name = 'FASHION & BEAUTY')),
    (uuid_generate_v4(), 'PERFUME', (SELECT id FROM categories WHERE name = 'FASHION & BEAUTY')),
    (uuid_generate_v4(), 'MEN`S FASHION', (SELECT id FROM categories WHERE name = 'FASHION & BEAUTY')),
    (uuid_generate_v4(), 'BABY & KIDS', (SELECT id FROM categories WHERE name = 'FASHION & BEAUTY')),

    (uuid_generate_v4(), 'TABLE & DESK', (SELECT id FROM categories WHERE name = 'FURNITURE & DECOR')),
    (uuid_generate_v4(), 'CHAIRS & SOFARS', (SELECT id FROM categories WHERE name = 'FURNITURE & DECOR')),
    (uuid_generate_v4(), 'OTHER FURNITURE', (SELECT id FROM categories WHERE name = 'FURNITURE & DECOR')),

    (uuid_generate_v4(), 'BOOKS', (SELECT id FROM categories WHERE name = 'BOOKS, SPORT & HOBBIES')),
    (uuid_generate_v4(), 'MUSICAL INSTRUMENTS', (SELECT id FROM categories WHERE name = 'BOOKS, SPORT & HOBBIES')),
    (uuid_generate_v4(), 'FISHING', (SELECT id FROM categories WHERE name = 'BOOKS, SPORT & HOBBIES')),

    (uuid_generate_v4(), 'HOUSE FOR SALES', (SELECT id FROM categories WHERE name = 'HOME AND LANDS')),
    (uuid_generate_v4(), 'HOUSE FOR RENTS', (SELECT id FROM categories WHERE name = 'HOME AND LANDS')),
    (uuid_generate_v4(), 'LAND FOR SALES', (SELECT id FROM categories WHERE name = 'HOME AND LANDS')),

    (uuid_generate_v4(), 'DOGS', (SELECT id FROM categories WHERE name = 'PETS')),
    (uuid_generate_v4(), 'CATS', (SELECT id FROM categories WHERE name = 'PETS')),
    (uuid_generate_v4(), 'BIRDS', (SELECT id FROM categories WHERE name = 'PETS'));
