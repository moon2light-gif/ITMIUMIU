SELECT people."ИМЯ", veds."ДАТА", ses."ЧЛВК_ИД" FROM "Н_ЛЮДИ" AS people
RIGHT OUTER JOIN "Н_ВЕДОМОСТИ" veds 
    ON veds."ЧЛВК_ИД" = people."ИД"
RIGHT OUTER JOIN "Н_СЕССИЯ" ses 
    ON ses."ЧЛВК_ИД" = veds."ЧЛВК_ИД"
WHERE people."ФАМИЛИЯ" > 'Ёлкин'
    AND veds."ИД" < 1490007
    AND ses."ДАТА" > TO_DATE('2002-01-04', 'YYYY-MM-DD');

