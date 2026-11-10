<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
    </head>

    <body>
        <h1>Произошла ошибка</h1>

        <p>Код ошибки: ${requestScope['jakarta.servlet.error.status_code']}</p>
        <p>URI: ${requestScope['jakarta.servlet.error.request_uri']}</p>
    </body>
</html>