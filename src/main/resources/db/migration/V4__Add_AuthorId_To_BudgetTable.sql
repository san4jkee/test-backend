ALTER TABLE BudgetTable
ADD COLUMN author_id INT REFERENCES Author(id);
