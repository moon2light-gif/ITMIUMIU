const STORAGE_KEY = "!!!mishka_lab1_history!!!";
const SIZE = 540; // Размер Canvas в px
const MID = SIZE / 2; // Начало координат

const UNIT = 40;
let currentR = null;

// Последняя точка, которая была отрисована
let lastPoint = null;

function checkHit(x, y, r) {
  if (x >= 0 && y >= 0) return x * x + y * y <= r * r; // Четверть круга
  if (x >= 0 && y <= 0) return x <= r && y >= -r; // Квадрат в IV четверти
  if (x <= 0 && y >= 0) return y <= x + r / 2; // Треугольник во II четверти
  return false; // Если точка попала в любое другое место
}

/** Перевод координат задачи в пиксели canvas. Ось Y на экране идёт вниз, поэтому минус. */
const toPx = (x) => MID + x * UNIT;
const toPy = (y) => MID - y * UNIT;

// Функция, которая вызывается при загрузке, при смене R и после добавления точки
function drawScene() {
  const canvas = document.getElementById("MyFirstCanvas");
  const ctx = canvas.getContext("2d");

  // Первым делом стираем старое изображение
  ctx.clearRect(0, 0, SIZE, SIZE);

  if (currentR !== null) {
    drawShapes(ctx, currentR);
  }

  drawAxes(ctx, currentR);
  drawPoint(ctx);
}

/**
 * Фукнкция для отрисовки фигур
 * Рисую перед осями, чтобы оси были поверх фигурок
 */
function drawShapes(ctx, r) {
  ctx.fillStyle = "rgba(99, 173, 225, 0.52)";
  ctx.strokeStyle = "blue";
  ctx.lineWidth = 2;

  // Треугольник
  ctx.beginPath();
  ctx.moveTo(toPx(-r / 2), toPy(0));
  ctx.lineTo(toPx(0), toPy(r / 2));
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
  ctx.arc(toPx(0), toPy(0), r * UNIT, -Math.PI / 2, 0);
  ctx.closePath();
  ctx.fill();
  ctx.stroke();
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

function drawPoint(ctx) {
  if (!lastPoint) return; // Возврат, если точка ещё не задана

  // По умолчанию точка серого цвета
  let color = "gray";
  if (currentR !== null) {
    const isHit = checkHit(lastPoint.x, lastPoint.y, currentR);
    if (isHit) color = "red";
  }

  //Отрисовка точки
  ctx.beginPath();
  ctx.arc(toPx(lastPoint.x), toPy(lastPoint.y), 5, 0, 2 * Math.PI);
  ctx.fillStyle = color;
  ctx.fill();

  ctx.strokeStyle = "black";
  ctx.lineWidth = 1.5;
  ctx.stroke();
}

// Функция для кнопки x, зажата может быть только одна
function setupXButtons() {
  const buttons = document.querySelectorAll(".x-btn");
  buttons.forEach((btn) => {
    btn.addEventListener("click", () => {
      buttons.forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");
      showError("x-error", "");
    });
  });
}

// Функция, которая выключает остальные чекбоксы, если один уже выбран
function setupCheckboxBehavior() {
  const boxes = document.querySelectorAll(".r-checkbox");
  boxes.forEach((cb) => {
    cb.addEventListener("change", (e) => {
      if (e.target.checked) {
        boxes.forEach((other) => {
          if (other !== e.target) other.checked = false;
        });
      }

      // Обновляем текущее значение R сразу же и отрисовываем
      const checked = document.querySelector(".r-checkbox:checked");
      currentR = checked ? Number(checked.value) : null;
      drawScene();
    });
  });
}

// Функция для валидации введённых значений
function validateForm() {
  clearErrors();
  let ok = true;

  const selectedX = document.querySelector(".x-btn.active");
  if (!selectedX) {
    showError("x-error", "Выберите координату X!");
    ok = false;
  }

  const selectedY = document
    .getElementById("y-field")
    .value.trim()
    .replace(",", ".");
  let y = NaN;
  if (selectedY === "") {
    showError("y-error", "Заполните поле Y!");
    ok = false;
  } else if (!/^-?\d+(\.\d+)?$/.test(selectedY)) {
    showError("y-error", "Y должен быть числом!");
    ok = false;
  } else {
    y = Number(selectedY);
    if (y < -3 || y > 3) {
      showError("y-error", "Y должен быть в диапазоне от -3 до 3!");
      ok = false;
    }
  }

  // R: ровно одно значение
  if (currentR === null) {
    showError("r-error", "Выберите значение R!");
    ok = false;
  }

  return ok ? { x: Number(selectedX.value), y, r: currentR } : null;
}

function handleFormSubmit(event) {
  event.preventDefault(); // не даём форме перезагрузить страницу

  const data = validateForm();
  if (!data) return;

  const record = {
    x: data.x,
    y: data.y,
    r: data.r,
    hit: checkHit(data.x, data.y, data.r),
    timestamp: Date.now(),
  };

  appendRowToResultTable(record);
  saveResult(record);

  // Добавляем точку
  lastPoint = { x: record.x, y: record.y };
  drawScene();
  playFeedbackAudio(record.hit);
}

function showError(elementId, message) {
  const el = document.getElementById(elementId);
  if (el) el.textContent = message;
}

function clearErrors() {
  document
    .querySelectorAll(".error-message")
    .forEach((el) => (el.textContent = ""));
}

// Функция для правильного отображения времени (для разных часовых поясов и для отображения в норм формате)
function formatTime(ms) {
  return new Date(ms).toLocaleString("ru-RU");
}

// Важнейшая функция добавления строки с результатами
function appendRowToResultTable(rec) {
  const tbody = document.getElementById("result-table-body");
  const row = document.createElement("tr");
  row.className = "results-row";

  const cells = [
    rec.x,
    rec.y,
    rec.r,
    rec.hit ? "Попадание" : "Промах",
    formatTime(rec.timestamp),
  ];

  cells.forEach((value, i) => {
    const td = document.createElement("td");
    td.textContent = value;
    if (i === 3) td.className = rec.hit ? "hit" : "miss";
    row.appendChild(td);
  });

  // Присоединяем строку с резами
  tbody.appendChild(row);
}

// Для восстановления прошлых данных
function readHistory() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) || [];
  } catch {
    return [];
  }
}

// Метод сохранения записи в localStorage
function saveResult(record) {
  const history = readHistory();
  history.push(record);
  localStorage.setItem(STORAGE_KEY, JSON.stringify(history));
}

// Восстанавливает таблицу и последнюю точку на canvas после перезагрузки
function loadResults() {
  const history = readHistory();
  history.forEach((rec) => {
    appendRowToResultTable(rec);
    lastPoint = { x: rec.x, y: rec.y };
  });
}

// Функция для воспроизведения звука
function playFeedbackAudio(isHit) {
  const audio = document.getElementById(isHit ? "audioOnHit" : "audioOnLose");
  if (audio) {
    audio.currentTime = 0;
    audio.play().catch(() => {}); // тут catch, чтобы браузер не запрещал автопроигрывание
  }
}

// Сборка результатов
document.addEventListener("DOMContentLoaded", () => {
  loadResults();
  setupXButtons();
  setupCheckboxBehavior();
  drawScene();
  document
    .getElementById("point-form")
    .addEventListener("submit", handleFormSubmit);
  document
    .getElementById("y-field")
    .addEventListener("input", () => showError("y-error", ""));
});
