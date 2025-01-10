-- Заменить 'Комиссия' на 'Расход' в таблице BudgetRecords
UPDATE BudgetRecords
SET type = 'Расход'
WHERE type = 'Комиссия';
