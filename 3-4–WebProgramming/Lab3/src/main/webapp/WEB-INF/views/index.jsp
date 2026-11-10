<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.mishka.model.ResultRecord" %>

<%
    List<ResultRecord> history =
            (List<ResultRecord>) request.getAttribute("history");

    if (history == null) {
        history = Collections.emptyList();
    }
%>

<!DOCTYPE html>
<html lang="ru">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width" />
    <title>Лабораторная работа №2</title>

    <audio id="audioOnHit" src="resources/audio/audioOnHit.mp3"></audio>
    <audio id="audioOnLose" src="resources/audio/audioOnMiss.mp3"></audio>
    <audio id="audioOnInvalidInput" src="resources/audio/audioOnInvalidData.mp3"></audio>
    <audio id="audioOnClear" src="resources/audio/audioOnClear.mp3"></audio>

    <script type="module" src="resources/js/main.js"></script>

    <!--Стилизация внутри веб-документа-->
    <link rel="stylesheet" href="resources/css/style.css"/>
  </head>

  <body data-context-path="${pageContext.request.contextPath}">
    <form
        id="point-form"
        action="${pageContext.request.contextPath}/controller"
        method="GET" >
    </form>

    <table class="outer-table">
      <tr>
        <td class="side-cell">
          <a href="https://itmo.ru/"><img src="resources/media/LogoITMO.png" alt="Контора"/></a>
          <video id="left" src="resources/media/binance.mp4" loop muted playsinline autoplay alt="Бинанс контора"></video>
          <a href="https://contract.gosuslugi.ru/"><img src="resources/media/YandexPicture.jpg" alt="Реклама Яндекс" /></a>
        </td>

        <td class="center-cell">
          <!--Табличная вёрстка моего веб-сайта-->
          <table
            border="1"
            cellspacing="1"
            cellpadding="5"
            style="max-width: 2000px; min-width: 500px; table-layout: fixed"
          >
            <!--Шапка сайта с указанием всего нужного-->
            <tr>
              <th class="top-header" colspan="6">
                <div class="th-inner">
                <span>ФИО: Крамарь Михаил Романович</span>
                <div class="right-side">
                  <span>Группа: P3213 | Вариант: 14</span>
                  <input type="range" id="volume" min="0" max="2" value="1" step="0.01" maxlength="2"/>
                  <button id="sound-button">
                    <img src="resources/media/sound.png" class="button-icon"/>
                  </button>
                </div>
                </div>
              </th>
            </tr>

            <tr>
              <th class="header" width="65%" colspan="4">Область координат</th>
              <th class="header" width="35%" colspan="2">Параметры</th>
            </tr>

            <!--Ряд ячеек, содержащий Canvas-->
            <tr>
              <td rowspan="5" colspan="4" class="canvas-container">
                <canvas id="MyFirstCanvas" width="540" height="540"> </canvas>
              </td>
            </tr>

              <tr>
                <td id="x-field-data" colspan="2">
                  <label class="x-label"><b>Изменение X (-5...3):</b></label
                  ><br />
                  <input
                    type="text"
                    id="x-field"
                    name="x"
                    placeholder="Вещественное число от -5 до 3"
                    maxlength="10"
                    style="width: 75%"
                    form="point-form"
                  />
                  <!-- Блок для отображения ошибок при валидации-->
                  <div id="x-error" class="error-message"></div>
                </td>
              </tr>

              <!--Ряд с полем для ввода координаты Y-->
              <tr>
                <td id="y-field-data" colspan="2">
                  <label class="y-label"><b>Изменение Y (-5...5):</b></label
                  ><br />
                  <input
                    type="text"
                    maxlength="6"
                    id="y-field"
                    name="y"
                    placeholder="Вещественное число от -5 до 5"
                    maxlength="10"
                    style="width: 75%"
                    form="point-form"
                  />
                  <div id="y-error" class="error-message"></div>
                </td>
              </tr>

              <!--Ряд с полем для ввода значения R-->
              <tr>
                <td id="r-button-group-data" colspan="2">
                  <label for="r-buttons" class="r-label"><b>Изменение R:</b></label>
                  <div id="r-buttons">
                    <input type="radio" id="r-btn-1"  name="r" form="point-form" value="1">1</input>
                    <input type="radio" id="r-btn-2"  name="r" form="point-form" value="1.5">1.5</input>
                    <input type="radio" id="r-btn-3"  name="r" form="point-form" value="2">2</input>
                    <input type="radio" id="r-btn-4"  name="r" form="point-form" value="2.5">2.5</input>
                    <input type="radio" id="r-btn-5"  name="r" form="point-form" value="3">3</input>
                  </div>
                  <div id="r-error" class="error-message"></div>
                </td>
              </tr>

              <!--Кнопка отправить резы и очистить историю-->
              <tr>
                <td id="submit-button-data" colspan="2">
                  <input type="submit" id="submit-button" form="point-form" value="Отправить" />
                  <input type="button" id="clear-button" value="Очистить историю" />
                  <div id="server-error" class="error-message"></div>
                </td>
              </tr>

            <!--Заголовок таблицы результатов-->
            <tr>
              <th class="header" colspan="6">Таблица результатов</th>
            </tr>

            <!--Ряд с подписью результатов-->
            <tr class="result-labels">
              <td>Значение X</td>
              <td>Значение Y</td>
              <td>Значение R</td>
              <td>Результат</td>
              <td>Время отправки</td>
              <td>Время выполнения</td>
            </tr>

            <!--Результаты-->
            <tbody id="result-table-body">
                <%
                    for (int i = history.size() - 1; i >= 0; i--) {
                        ResultRecord record = history.get(i);
                %>

                <tr class="results-row"
                    data-x="<%= record.x() %>"
                    data-y="<%= record.y() %>"
                    data-r="<%= record.r() %>"
                    data-hit="<%= record.isHit() %>"
                    data-timestamp="<%= record.currentTime() %>">

                    <td><%= record.x() %></td>
                    <td><%= record.y() %></td>
                    <td><%= record.r() %></td>

                    <td class="<%= record.isHit() ? "hit" : "miss" %>">
                        <%= record.isHit() ? "Попадание" : "Промах" %>
                    </td>

                    <td class="timestamp"></td>

                    <td><%= record.executionTime() %> мс</td>

                </tr>

                <%
                    }
                %>

            </tbody>
            
          </table>
        </td>

        <td class="side-cell">
          <video id="right" src="resources/media/subway_mixed.mp4" loop muted playsinline autoplay alt="Subway Serfers"></video>
          <a href="https://xn--80advebqectngd.xn--p1ai/"><img src="resources/media/RightPicture.jpeg" alt="RightAd" /></a>
        </td>
      </tr>
    </table>
  </body>
</html>
