SELECT studs."ГРУППА", studs."ИД", people."ФАМИЛИЯ", people."ИМЯ", 
people."ОТЧЕСТВО", studs."П_ПРКОК_ИД" FROM "Н_УЧЕНИКИ" AS studs
INNER JOIN "Н_ЛЮДИ" AS people 
    ON studs."ЧЛВК_ИД" = people."ИД"
INNER JOIN "Н_ПЛАНЫ" AS pl
    ON pl."ИД" = studs."ПЛАН_ИД"
INNER JOIN "Н_ФОРМЫ_ОБУЧЕНИЯ" AS forms
    ON forms."ИД" = pl."ФО_ИД"
WHERE forms."НАИМЕНОВАНИЕ" = 'Очная'
    AND studs."ПРИЗНАК" = 'отчисл'
    AND CAST(studs."КОНЕЦ" AS DATE) = TO_DATE('2012-09-01', 'YYYY-MM-DD');
-- скорее всего просто нет людей, отчислившихся ровно первого сентября 2012 года

