SELECT DISTINCT "ПЛАН_ИД" FROM "Н_ГРУППЫ_ПЛАНОВ"
--distinct нужен, чтобы не видеть удвоенные номера групп
WHERE "ПЛАН_ИД" IN (
    SELECT "ПЛАН_ИД" FROM "Н_ПЛАНЫ"
    WHERE "ФО_ИД" IN (
        SELECT "ИД" FROM "Н_ФОРМЫ_ОБУЧЕНИЯ"
        WHERE "НАИМЕНОВАНИЕ" = 'Очная'
    )
) AND "ПЛАН_ИД" IN (
    SELECT "ПЛАН_ИД" FROM "Н_ГРУППЫ_ПЛАНОВ"
    GROUP BY "ПЛАН_ИД"
    HAVING COUNT(*) = 2
);
