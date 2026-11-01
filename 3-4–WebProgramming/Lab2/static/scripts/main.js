const SIZE = document.querySelector("canvas").height; // Размер Canvas в px
const MID = SIZE / 2; // Начало координат

const UNIT = 50;

// Глобальная переменная для хранения текущего значения R
let currentR = null;
// Последняя точка, которая была   отрисована
let lastPoint = null;

/** Перевод координат задачи в пиксели canvas. Ось Y на экране идёт вниз, поэтому минус. */
const toPx = (x) => MID + x * UNIT;
const toPy = (y) => MID - y * UNIT;

// Функция, которая вызывается при загрузке, при смене R и после добавления точки
function drawScene() {
  const canvas = document.getElementById("MyFirstCanvas");
  const ctx = canvas.getContext("2d");

  // Первым делом стираем старое изображение
  ctx.clearRect(0, 0, SIZE, SIZE);

  // Если есть введённое значение R - рисуем саму фигуру
  if (currentR !== null) {
    drawShape(ctx, currentR);
  }

  drawAxes(ctx, currentR);
  drawPoint(ctx);
}

/**
 * Фукнкция для отрисовки фигур
 * Рисую перед осями, чтобы оси были поверх фигурок
 */
function drawShape(ctx, r) {
  ctx.fillStyle = "rgba(117, 198, 255, 0.6)";
  ctx.strokeStyle = "blue";
  ctx.lineWidth = 2;

  // Бетмен
  ctx.beginPath();
  // Горизонтальная линия - голова
  ctx.moveTo(toPx(r / 16), toPy((3 / 8) * r));
  ctx.lineTo(toPx(-r / 16), toPy((3 / 8) * r));

  // Левое ухо
  ctx.lineTo(toPx(-r / 8), toPy(r / 2));
  ctx.lineTo(toPx(-(3 / 16) * r), toPy((3 / 16) * r));

  // Верхняя выемка
  ctx.bezierCurveTo(
    toPx((-5 / 16) * r),
    toPy((3 / 32) * r),
    toPx(-r / 2),
    toPy((3 / 16) * r),
    toPx(-r / 2),
    toPy(r / 2),
  );

  // Левое крыло
  ctx.bezierCurveTo(
    toPx((-r * 7) / 6),
    toPy(r / 4),
    toPx((-r * 7) / 6),
    toPy(-r / 4),
    toPx(-r / 2),
    toPy(-r / 2),
  );

  // Две нижние выемки
  ctx.bezierCurveTo(
    toPx(-r / 2),
    toPy((-7 / 32) * r),
    toPx((-9 / 32) * r),
    toPy((-7 / 32) * r),
    toPx(-r / 4),
    toPy((-7 / 16) * r),
  );
  ctx.bezierCurveTo(
    toPx(-r / 4),
    toPy(-r / 4),
    toPx(-r / 16),
    toPy(-r / 4),
    toPx(0),
    toPy(-r / 2),
  );

  // Зеркальная правая часть мышки
  ctx.bezierCurveTo(
    toPx(r / 16),
    toPy(-r / 4),
    toPx(r / 4),
    toPy(-r / 4),
    toPx(r / 4),
    toPy((-7 / 16) * r),
  );
  ctx.bezierCurveTo(
    toPx((9 / 32) * r),
    toPy((-7 / 32) * r),
    toPx(r / 2),
    toPy((-7 / 32) * r),
    toPx(r / 2),
    toPy(-r / 2),
  );

  // Правое крыло
  ctx.bezierCurveTo(
    toPx((r * 7) / 6),
    toPy(-r / 4),
    toPx((r * 7) / 6),
    toPy(r / 4),
    toPx(r / 2),
    toPy(r / 2),
  );

  // Правая верхняя выемка
  ctx.bezierCurveTo(
    toPx(r / 2),
    toPy((3 / 16) * r),
    toPx((5 / 16) * r),
    toPy((3 / 32) * r),
    toPx((3 / 16) * r),
    toPy((3 / 16) * r),
  );
  // Наконец-то правое ухо
  ctx.lineTo(toPx(r / 8), toPy(r / 2));
  ctx.lineTo(toPx(r / 16), toPy((3 / 8) * r));
  ctx.stroke();

  // Закрашиваем бетмена внутри
  ctx.fill();
}

// Функция для отрисовки осей
function drawAxes(ctx, r) {
  const t = r === null ? 4 : r; // где рисовать засечки, если R не выбран
  const label = (k) => (r === null ? (k === 1 ? "R" : "R/2") : String(r * k));

  ctx.strokeStyle = "black";
  ctx.fillStyle = "black";
  ctx.lineWidth = 1;
  ctx.font = "15px Arial";

  ctx.beginPath();
  ctx.moveTo(MID + 0.5, 0);
  ctx.lineTo(MID + 0.5, SIZE);
  ctx.moveTo(0, MID + 0.5);
  ctx.lineTo(SIZE, MID + 0.5);

  // Засечки: R и R/2 на обеих осях
  for (const k of [1, 0.5, -0.5, -1]) {
    const p = t * k * UNIT;
    ctx.moveTo(MID + p + 0.5, MID - 4);
    ctx.lineTo(MID + p + 0.5, MID + 4);
    ctx.moveTo(MID - 4, MID - p + 0.5);
    ctx.lineTo(MID + 4, MID - p + 0.5);
  }
  ctx.stroke();

  // Стрелки на концах осей
  ctx.beginPath();
  ctx.moveTo(MID - 4, 14);
  ctx.lineTo(MID, 0);
  ctx.lineTo(MID + 4, 14);
  ctx.fill();
  ctx.beginPath();
  ctx.moveTo(SIZE - 14, MID - 4);
  ctx.lineTo(SIZE, MID);
  ctx.lineTo(SIZE - 14, MID + 4);
  ctx.fill();

  ctx.fillText("Y", MID + 10, 14);
  ctx.fillText("X", SIZE - 14, MID - 10);

  // Подписи засечек
  for (const k of [1, 0.5]) {
    const p = t * k * UNIT;
    const text = label(k);
    ctx.fillText(text, MID + p - 4, MID - 8); //  +значение на X
    ctx.fillText("-" + text, MID - p - 10, MID - 8); // -значение на X
    ctx.fillText(text, MID + 8, MID - p + 5); //  +значение на Y
    ctx.fillText("-" + text, MID + 8, MID + p + 5); // -значение на Y
  }
}

// Функция отрисовки точки
function drawPoint(ctx) {
  if (!lastPoint) return; // Возврат, если точка ещё не задана

  // Красная только если было попадание
  const color = lastPoint.hit ? "red" : "gray";

  //Отрисовка точки
  ctx.beginPath();
  ctx.arc(toPx(lastPoint.x), toPy(lastPoint.y), 5, 0, 2 * Math.PI);
  ctx.fillStyle = color;
  ctx.fill();

  ctx.strokeStyle = "black";
  ctx.lineWidth = 1.5;
  ctx.stroke();
}

// Функция для очистки истории запросов
async function handleClearButton() {
  const url = "/fcgi?action=clear";
  try {
    const response = await fetch(url, { method: "GET" });

    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    // Очищаем текущую отрисованную таблицу
    document.getElementById("result-table-body").innerHTML = "";
    items = [];
    currentPage = 0;
    document.querySelector(".pagination")?.remove();
    playClearHistoryAudio();
    // Текущее значение R и точку выставляем неизвестными
    currentR = null;
    lastPoint = null;
    drawScene();
  } catch (error) {
    console.error("Не удалось очистить историю запросов: ", error.message);
  }
}

// Функция для обработки формы
async function handleFormSubmit(event) {
  event.preventDefault();

  const formData = new FormData(event.currentTarget);

  const searchParams = new URLSearchParams({
    x: formData.get("radio-x") ?? "",
    y: formData.get("y-field") ?? "",
    r: formData.get("r-field") ?? "",
  });

  const queryString = searchParams.toString();

  const url = `/fcgi?${queryString}`;
  try {
    // За счёт ключевого слова await сразу получим объект Response
    const response = await fetch(url, {
      method: "GET",
    });

    if (!response.ok) {
      const body = await response.json().catch(() => ({}));
      showErrors(body.errors || { server: `Ошибка ${response.status}` });
      playInvalidInputAudio();
      return;
    }

    // Очищаем ошибки (пустые div показываем)
    showErrors();

    // Получаем полностью корректный результат в виде json-строки
    const record = await response.json();

    // Добавляем запись в таблицу
    pushRowToResultTable(record);

    currentR = record.r;
    // Перезаписываем координаты последней точки для отрисовки
    lastPoint = { x: record.x, y: record.y, hit: record.hit };
    drawScene();
    playHitAudio(record.hit);
  } catch (error) {
    // Если произошла прям критическая ошибка, например, интернет упал или ЦОД лежит
    showErrors({ server: "Сервер недоступен" });
    console.error(error.message);
  }
}

function showError(elementId, message) {
  const el = document.getElementById(elementId);
  if (el) el.textContent = message;
}

const ERROR_FIELDS = ["x", "y", "r", "server"];

// Функция для отображения ошибок, которые пришли в ответе веб-сервера
function showErrors(errors = {}) {
  for (const field of ERROR_FIELDS) {
    showError(`${field}-error`, errors[field] ?? "");
  }
}

// Функция для правильного отображения времени (для разных часовых поясов и для отображения в норм формате)
function formatTime(ms) {
  return new Date(ms).toLocaleString("ru-RU");
}

// Важнейшая функция добавления строки с результатами
function pushRowToResultTable(rec) {
  const tbody = document.getElementById("result-table-body");
  const row = document.createElement("tr");
  row.className = "results-row";

  const cells = [
    rec.x,
    rec.y,
    rec.r,
    rec.hit ? "Попадание" : "Промах",
    formatTime(rec.timestamp),
    rec.executionTime + " мс",
  ];

  cells.forEach((value, i) => {
    const td = document.createElement("td");
    td.textContent = value;
    if (i === 3) {
      td.className = rec.hit ? "hit" : "miss";
    }
    row.appendChild(td);
  });

  // Присоединяем строку с резами
  tbody.prepend(row);

  // Обнова
  items = Array.from(tbody.getElementsByTagName("tr"));
  document.querySelector(".pagination")?.remove();
  createPageButtons();
  currentPage = 0;
  showPage(currentPage);
}

// Восстанавливает таблицу и последнюю точку на Canvas после перезагрузки
async function loadResults() {
  try {
    const response = await fetch("/fcgi?action=history", { method: "GET" });
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    // Получаем все записи в виде json-строки
    const history = await response.json();
    history.forEach((elem) => {
      pushRowToResultTable(elem);
      lastPoint = { x: elem.x, y: elem.y, hit: elem.hit };
      currentR = elem.r;
    });
  } catch (error) {
    console.error("Не удалось загрузить историю запросов: ", error.message);
  }
}

// Функция для воспроизведения звука при промахе и попадании
function playHitAudio(isHit) {
  const audio = document.getElementById(isHit ? "audioOnHit" : "audioOnLose");
  if (audio) {
    audio.currentTime = 0;
    audio.play().catch(() => {}); // тут catch, чтобы браузер не запрещал автопроигрывание
  }
}

// Функция для проигрывания звука при некорректном вводе
function playInvalidInputAudio() {
  const audio = document.getElementById("audioOnInvalidInput");
  if (audio) {
    audio.currentTime = 0;
    audio.play().catch(() => {});
  }
}

// Проигрывание звука при очистке таблицы
function playClearHistoryAudio() {
  const audio = document.getElementById("audioOnClear");
  if (audio) {
    audio.currentTime = 0;
    audio.play().catch(() => {});
  }
}

// Пагинация таблички
const itemsPerPage = 10;
let currentPage = 0;
let items = [];

// Функция отображения одной страницы
function showPage(page) {
  const startIndex = page * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  items.forEach((item, index) => {
    item.classList.toggle("hidden", index < startIndex || index >= endIndex);
  });
  updateActiveButtonStates();
}

// Функция создания кнопок для переключения между страницами
function createPageButtons() {
  const totalPages = Math.ceil(items.length / itemsPerPage);

  document.querySelector(".pagination")?.remove();

  if (totalPages <= 1) return;

  const paginationContainer = document.createElement("div");
  paginationContainer.classList.add("pagination");

  for (let i = 0; i < totalPages; i++) {
    const pageButton = document.createElement("button");
    pageButton.textContent = i + 1;
    pageButton.addEventListener("click", () => {
      currentPage = i;
      showPage(currentPage);
    });
    paginationContainer.appendChild(pageButton);
  }

  const tbody = document.getElementById("result-table-body");
  const tableWrapper = tbody
    ? tbody.closest("table") || tbody.parentElement
    : document.body;
  tableWrapper.after(paginationContainer);

  updateActiveButtonStates();
}

// Функция обновления кнопок страниц при нажатии
function updateActiveButtonStates() {
  const pageButtons = document.querySelectorAll(".pagination button");
  pageButtons.forEach((button, index) => {
    if (index == currentPage) {
      button.classList.add("active");
    } else {
      button.classList.remove("active");
    }
  });
}

// Сборка результатов
document.addEventListener("DOMContentLoaded", async () => {
  const audioState = {
    left: document.getElementById("left"),
    right: document.getElementById("right"),
    audioContext: null,
    gainNode: null,
    volume: 1,
    enabled: false,
  };

  document
    .getElementById("sound-button")
    .addEventListener("click", handleSoundButton(audioState));

  document
    .getElementById("volume")
    .addEventListener("input", handleVolumeChange(audioState));

  document
    .getElementById("point-form")
    .addEventListener("submit", handleFormSubmit);

  document
    .getElementById("y-field")
    .addEventListener("input", () => showError("y-error", ""));

  document
    .getElementById("r-field")
    .addEventListener("input", () => showError("r-error", ""));

  document
    .getElementById("clear-button")
    .addEventListener("click", handleClearButton);

  // HTML-элемент, который содержит все наши строки результатов запросов
  const content = document.getElementById("result-table-body");

  // Функция для инициализации аудио
  function initAudio(audioState) {
    const audioContext = new AudioContext();
    const gainNode = audioContext.createGain();

    gainNode.gain.value = audioState.volume;
    gainNode.connect(audioContext.destination);

    const connect = (video, pan) => {
      audioContext
        .createMediaElementSource(video)
        .connect(new StereoPannerNode(audioContext, { pan }))
        .connect(gainNode);
    };

    // Панорамирование: -1 для левого видео, 1 - для правого
    connect(audioState.left, -1);
    connect(audioState.right, 1);

    audioState.audioContext = audioContext;
    audioState.gainNode = gainNode;
  }

  // Включаем звук на странице, используя Web Audio API
  function handleSoundButton(audioState) {
    return async () => {
      if (!audioState.audioContext) {
        initAudio(audioState);
        await audioState.audioContext.resume();
        audioState.left.muted = audioState.right.muted = false;
        audioState.enabled = true;
      } else {
        audioState.enabled = !audioState.enabled;
        audioState.gainNode.gain.value = audioState.enabled
          ? audioState.volume
          : 0;
      }
    };
  }

  // Регулировка громкости на странице
  function handleVolumeChange(audioState) {
    return (event) => {
      audioState.volume = Number(event.target.value);
      if (audioState.gainNode && audioState.enabled) {
        audioState.gainNode.gain.value = audioState.volume;
      }
    };
  }

  // Загрузка прошлых взаимодействий с сайтиком через веб-сервер
  try {
    await loadResults();
    items = Array.from(content.getElementsByTagName("tr"));
  } catch (error) {
    console.error(`Ошибка при загрузке результатов: ${error.message}`);
  }

  drawScene();
  createPageButtons();
  showPage(currentPage);
});
