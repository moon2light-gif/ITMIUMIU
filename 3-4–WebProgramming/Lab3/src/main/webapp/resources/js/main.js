let SIZE = document.querySelector("canvas").height; // Размер Canvas в px
let MID = SIZE / 2; // Начало координат
const UNIT = 75;

let currentR = null; // Глобальная переменная для хранения текущего значения R
let lastPoint = null; // Последняя точка, которая была отрисована

/** Перевод координат задачи в пиксели canvas. Ось Y на экране идёт вниз, поэтому минус. */
const toPx = (x) => MID + x * UNIT;
const toPy = (y) => MID - y * UNIT;

const reverseToPx = (x) => (x - MID) / UNIT;
const reverseToPy = (y) => (MID - y) / UNIT;

const canvas = document.getElementById("MyFirstCanvas");
const ctx = canvas.getContext("2d");

/** Функция для получения позиции мышки в координатах canvas,
 * с последующим переводом в текущие координаты
 */
function getMousePosition(event) {
  const rect = canvas.getBoundingClientRect();

  const scaleX = canvas.width / rect.width;
  const scaleY = canvas.height / rect.height;

  const rawX = (event.clientX - rect.left) * scaleX;
  const rawY = (event.clientY - rect.top) * scaleY;

  if (!currentR) {
    showError("r-error", "Невозможно определить координаты точки!");
    playInvalidInputAudio();
  } else {
    const x = reverseToPx(rawX);
    const y = reverseToPy(rawY);

    showError("r-error", "");

    if (x < -5 || x > 3 || y < -5 || y > 5) {
        showError("r-error", "Координаты точки выходят за допустимый диапазон!");
        playInvalidInputAudio();
        return;
    }

    sendCanvasRequest(x, y);
  }
}

/** Основная функция отрисовки всего, что расположено на Canvas,
 *  которая вызывается при загрузке, при смене R
 *  и после добавления точки */
function drawScene() {
  // Первым делом стираем старое изображение
  ctx.clearRect(0, 0, SIZE, SIZE);

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
  ctx.fillStyle = "rgba(58, 173, 255, 0.55)";
  ctx.strokeStyle = "rgb(0, 157, 255)";
  ctx.lineWidth = 2;

  // Треугольник
  ctx.beginPath();
  ctx.moveTo(toPx(-r), toPy(0));
  ctx.lineTo(toPx(0), toPy(r));
  ctx.lineTo(toPx(0), toPy(0));
  ctx.closePath();
  ctx.fill();
  ctx.stroke();

  // Квадратик
  ctx.beginPath();
  ctx.rect(toPx(0), toPy(0), r * UNIT, r * UNIT);
  ctx.fill();
  ctx.stroke();

  // Четверть круга
  ctx.beginPath();
  ctx.moveTo(toPx(0), toPy(0));
  ctx.arc(toPx(0), toPy(0), (r / 2) * UNIT, -Math.PI / 2, 0);
  ctx.fill();
  ctx.stroke();
}

// Функция для отрисовки осей
function drawAxes(ctx, r) {
  const t = r === null ? 3 : r; // где рисовать засечки, если R не выбран
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

/** Функция для отрисовки последней отправленной точки */
function drawPoint(ctx) {
  if (!lastPoint) {
    return; // Возврат, если точка ещё не задана
  }

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

/* Функция для валидации вводимых значений */
function validateForm() {
  let errors = {};
  let ok = true;

  const selectedX = document
    .getElementById("x-field")
    .value.trim()
    .replace(",", ".");
  let x = NaN;
  if (selectedX === "") {
    errors["x"] = "Заполните поле X!";
    ok = false;
  } else if (!/^-?\d+(\.\d+)?$/.test(selectedX)) {
    errors["x"] = "X должен быть вещественным числом!";
    ok = false;
  } else {
    x = Number(selectedX);
    if (x < -5 || x > 3) {
      errors["x"] = "X должен быть в диапазоне от -5 до 3!";
      ok = false;
    }
  }

  const selectedY = document
    .getElementById("y-field")
    .value.trim()
    .replace(",", ".");
  let y = NaN;
  if (selectedY === "") {
    errors["y"] = "Заполните поле Y!";
    ok = false;
  } else if (!/^-?\d+(\.\d+)?$/.test(selectedY)) {
    errors["y"] = "Y должен быть вещественным числом!";
    ok = false;
  } else {
    y = Number(selectedY);
    if (y < -5 || y > 5) {
      errors["y"] = "Y должен быть в диапазоне от -5 до 5!";
      ok = false;
    }
  }

  const selectedR = document.querySelector('input[name="r"]:checked');
  if (!selectedR) {
    errors["r"] = "Выберите значение R!";
    ok = false;
  } else {
    currentR = selectedR.value;
  }

  if (!ok) {
    showErrors(errors);
  }

  return ok;
}

function pushRowToResultTable(record) {
    const tbody = document.getElementById("result-table-body");

    const row = document.createElement("tr");
    row.classList.add("results-row");

    row.dataset.x = record.x;
    row.dataset.y = record.y;
    row.dataset.r = record.r;
    row.dataset.hit = record.hit;
    row.dataset.timestamp = record.timestamp;

    const xCell = document.createElement("td");
    xCell.textContent = record.x;

    const yCell = document.createElement("td");
    yCell.textContent = record.y;

    const rCell = document.createElement("td");
    rCell.textContent = record.r;

    const resultCell = document.createElement("td");
    resultCell.classList.add(record.hit ? "hit" : "miss");
    resultCell.textContent = record.hit ? "Попадание" : "Промах";

    const timeCell = document.createElement("td");
    timeCell.classList.add("timestamp");
    timeCell.textContent = formatTime(record.timestamp);

    const executionCell = document.createElement("td");
    executionCell.textContent = `${record.executionTime} мс`;

    row.append(
        xCell,
        yCell,
        rCell,
        resultCell,
        timeCell,
        executionCell
    );

    tbody.prepend(row);

    items.unshift(row);

    currentPage = 0;

    createPageButtons();
    showPage(currentPage);
}

async function sendCanvasRequest(x, y) {
    const contextPath = document.body.dataset.contextPath;

    const params = new URLSearchParams({
        x: x,
        y: y,
        r: currentR,
        ajax: "true"
    });

    try {
        const response = await fetch(
            `${contextPath}/controller?${params}`,
            {method: "GET"}
        );

        if (!response.ok) {
            throw new Error("Ошибка сервера: " + response.status);
        }

        const record = await response.json();

        pushRowToResultTable(record);

        lastPoint = {
            x: Number(record.x),
            y: Number(record.y),
            hit: record.hit
        };

        currentR = Number(record.r);

        drawScene();
        playHitAudio(record.hit);

    } catch (error) {
        console.error(error);
        showError("server-error", error.message);
    }
}

// Функция для очистки истории запросов
async function handleClearButton() {
  const contextPath = document.body.dataset.contextPath;
  const url = `${contextPath}/controller?action=clear`;
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

    // Текущее значение R и точку выставляем неизвестными
    currentR = null;
    lastPoint = null;

    drawScene();
    playClearHistoryAudio();
  } catch (error) {
    console.error("Не удалось очистить историю запросов: ", error.message);
  }
}

// Функция для отправки формы
function handleFormSubmit(event) {
  if (!validateForm()) {
    event.preventDefault();
    playInvalidInputAudio();
  }
}

const ERROR_FIELDS = ["x", "y", "r", "server"];

// Функция для отображения ошибок
function showErrors(errors = {}) {
  for (const field of ERROR_FIELDS) {
    showError(`${field}-error`, errors[field] ?? "");
  }
}

function showError(elementId, message) {
  const el = document.getElementById(elementId);
  if (el) el.textContent = message;
}

// Функция для правильного отображения времени (для разных часовых поясов и для отображения в норм формате)
function formatTime(ms) {
  return new Date(ms).toLocaleString("ru-RU");
}

function initializeHistory() {
    const tbody = document.getElementById("result-table-body");

    items = Array.from(tbody.getElementsByTagName("tr"));

    items.forEach((row) => {
        const timestamp = Number(row.dataset.timestamp);

        row.querySelector(".timestamp").textContent =
            formatTime(timestamp);
    });

    if (items.length > 0) {
        const newest = items[0];

        lastPoint = {
            x: Number(newest.dataset.x),
            y: Number(newest.dataset.y),
            hit: newest.dataset.hit === "true"
        };

        currentR = Number(newest.dataset.r);

        const selectedRadio = document.querySelector(
            `input[name="r"][value="${currentR}"]`
        );

        if (selectedRadio) {
            selectedRadio.checked = true;
        }
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

  document.body.addEventListener("click", (event) => {
    if (event.target && event.target.type == "radio") {
      showError("r-error", "");
      currentR = Number(event.target.value);
      drawScene();
    }
  });

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
    .getElementById("x-field")
    .addEventListener("input", () => showError("x-error", ""));

  document
    .getElementById("clear-button")
    .addEventListener("click", handleClearButton);

  document
    .getElementById("MyFirstCanvas")
    .addEventListener("mousedown", (event) => {
      getMousePosition(event);
    });

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

  initializeHistory();

  drawScene();
  createPageButtons();
  showPage(currentPage);
});
