SELECT people."ИМЯ", people."ФАМИЛИЯ", people."ОТЧЕСТВО" FROM "Н_ЛЮДИ" AS people
LEFT OUTER JOIN "Н_УЧЕНИКИ" AS studs 
    ON studs."ЧЛВК_ИД" = people."ИД"
WHERE studs."ЧЛВК_ИД" IS NULL;
-- тут на самом деле все поля studs у таких людей будут null, просто берём самое очевидное - ЧЛВК_ИД

