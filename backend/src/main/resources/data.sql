INSERT INTO category_rules (keyword, category)
SELECT v.keyword, v.category
FROM (VALUES
    ('GROCERY', 'Groceries'), ('KROGER', 'Groceries'),
    ('COFFEE', 'Dining'), ('STARBUCKS', 'Dining'), ('PIZZA', 'Dining'),
    ('RESTAURANT', 'Dining'), ('UBER EATS', 'Dining'),
    ('UBER', 'Transport'), ('SHELL', 'Transport'), ('FUEL', 'Transport'),
    ('AMAZON', 'Shopping'), ('TARGET', 'Shopping'),
    ('NETFLIX', 'Subscriptions'), ('SPOTIFY', 'Subscriptions'),
    ('ELECTRIC', 'Utilities'),
    ('PAYCHECK', 'Income'), ('SALARY', 'Income')
) AS v(keyword, category)
WHERE NOT EXISTS (SELECT 1 FROM category_rules r WHERE r.keyword = v.keyword);