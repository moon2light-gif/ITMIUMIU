package client.ui;

import client.utility.LanguageManager;
import common.model.Coordinates;
import common.model.Organization;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Класс {@code CityMapPane} — область визуализации коллекции в виде карты города
 *
 * @author mikhailkramar
 */
public class CityMapPane extends Pane {

    /**
     * функциональный интерфейс для фиксации перемещений наших кружков
     */
    public interface MoveCommitter {
        void commit(Organization org, float newX, long newY);
    }

    private static final double X_MIN = -694, X_MAX = 748;
    private static final double Y_MIN = -748, Y_MAX = 748;

    private static final double[][] POINTS = {{-450, 500}, {250, -300}, {550, 600}};
    private static final Color POINT_COLOR = Color.web("#e03131");
    private static final double POINT_R = 11;
    private static final double POINT_SNAP = 14;
    private static final double POINT_EPS = 1.0;

    private static final double MIN_R = 9, MAX_R = 26;

    private final Canvas canvas = new Canvas();
    private final Image mapImage = loadMapImage();
    private final Pane slotsLayer = new Pane();
    private final Pane objectsLayer = new Pane();
    private final Pane legendLayer = new Pane();
    private final Text hintText = new Text();
    private final Text emptyText = new Text();

    private final LanguageManager lm = LanguageManager.getInstance();

    private List<Organization> orgs = new ArrayList<>();
    private Long myOwnerId;

    private final Map<Long, Circle> circleById = new HashMap<>();
    private final Set<Long> knownIds = new HashSet<>();   // для анимации появления

    private Consumer<Organization> onObjectClicked = o -> { };
    private MoveCommitter onMoveCommitted = (o, x, y) -> { };
    private Consumer<String> showMessage = m -> { };
    private Predicate<Organization> canDrag = o -> true;

    public CityMapPane() {
        getStyleClass().add("city-map");
        slotsLayer.setMouseTransparent(true);
        legendLayer.setMouseTransparent(true);
        getChildren().addAll(canvas, slotsLayer, objectsLayer, legendLayer, hintText, emptyText);

        hintText.getStyleClass().add("map-hint");
        hintText.setFont(Font.font("System", 12));
        emptyText.setFont(Font.font("System", FontWeight.BOLD, 16));
        emptyText.setFill(Color.web("#868e96"));

        localize();
    }

    public void setCallbacks(Consumer<Organization> onClicked, MoveCommitter onMoved,
                             Consumer<String> messageSink, Predicate<Organization> draggable) {
        this.onObjectClicked = onClicked;
        this.onMoveCommitted = onMoved;
        this.showMessage = messageSink;
        this.canDrag = draggable;
    }

    /**
     * Метод для обновления данных карты (постоянно вызывается при загрузке и опросе сервера)
     */
    public void setData(List<Organization> data, Long myOwnerId) {
        this.orgs = (data == null) ? new ArrayList<>() : new ArrayList<>(data);
        this.myOwnerId = myOwnerId;
        buildLegend(); //перестраиваем легенду, когда владелец уже известен, чтобы её цвет совпадал с цветом твоих кружков
        rebuildObjects();
    }

    /**
     * Метод для обновления текста при смене языка
     */
    public void localize() {
        hintText.setText(lm.get("map.hint"));
        emptyText.setText(lm.get("map.empty"));
        buildLegend();
        rebuildSlots();
        requestLayout();
    }

    private double toPixelX(double logicalX) {
        return (logicalX - X_MIN) / (X_MAX - X_MIN) * getWidth();
    }

    private double toPixelY(double logicalY) {
        return getHeight() - (logicalY - Y_MIN) / (Y_MAX - Y_MIN) * getHeight();
    }

    private float toLogicalX(double pixelX) {
        double v = X_MIN + (pixelX / Math.max(1, getWidth())) * (X_MAX - X_MIN);
        v = Math.max(X_MIN + 1, Math.min(X_MAX, v));
        return (float) v;
    }

    private long toLogicalY(double pixelY) {
        double v = Y_MIN + ((getHeight() - pixelY) / Math.max(1, getHeight())) * (Y_MAX - Y_MIN);
        v = Math.max(Y_MIN, Math.min(Y_MAX, v));
        return Math.round(v);
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth(), h = getHeight();
        canvas.setWidth(w);
        canvas.setHeight(h);
        drawBackground();

        for (int i = 0; i < POINTS.length; i++) {
            Circle dot = (Circle) slotsLayer.getChildren().get(i * 2);
            Text t = (Text) slotsLayer.getChildren().get(i * 2 + 1);
            double cx = toPixelX(POINTS[i][0]), cy = toPixelY(POINTS[i][1]);
            dot.setCenterX(cx);
            dot.setCenterY(cy);
            t.setX(cx - t.getLayoutBounds().getWidth() / 2);
            t.setY(cy + 4);
        }

        for (Organization o : orgs) {
            Circle c = circleById.get(o.getId());
            if (c == null || Boolean.TRUE.equals(c.getProperties().get("dragging"))) continue;
            c.setCenterX(toPixelX(o.getCoordinates().getX()));
            c.setCenterY(toPixelY(o.getCoordinates().getY()));
        }

        hintText.setX(16);
        hintText.setY(h - 14);
        legendLayer.relocate(14, 14);
        emptyText.setVisible(orgs.isEmpty());
        emptyText.setX(w / 2 - emptyText.getLayoutBounds().getWidth() / 2);
        emptyText.setY(h / 2);
    }

    /**
     * Лютейший метод для отображения нашей яндекс карты
     * @return картинка, на которой изображена карта
     */
    private static Image loadMapImage() {
        try {
            return new Image(CityMapPane.class.getResourceAsStream("/img/citymap.png"));
        } catch (Exception ex) {
            return null;
        }
    }

    private void drawBackground() {
        double w = getWidth(), h = getHeight();
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, w, h);

        if (mapImage != null && !mapImage.isError()) {
            g.drawImage(mapImage, 0, 0, w, h);
            g.setFill(Color.web("#ffffff", 0.14));
            g.fillRect(0, 0, w, h);
        } else {
            g.setFill(Color.web("#eef2e6"));
            g.fillRect(0, 0, w, h);
        }

        g.setStroke(Color.web("#b9c2cc"));
        g.setLineWidth(2);
        g.strokeRect(1, 1, w - 2, h - 2);
    }

    /**
     * Метод для построения легендарных точек "притяжения"
     * (те красные точечки на карте)
     */
    private void rebuildSlots() {
        slotsLayer.getChildren().clear();
        for (int i = 0; i < POINTS.length; i++) {
            Circle dot = new Circle(POINT_R);
            dot.setFill(POINT_COLOR);
            dot.setStroke(Color.web("#ffffff"));
            dot.setStrokeWidth(2);
            Text t = new Text(String.valueOf(i + 1));
            t.setFont(Font.font("System", FontWeight.BOLD, 12));
            t.setFill(Color.WHITE);
            slotsLayer.getChildren().addAll(dot, t);
        }
    }

    private int slotAt(double pixelX, double pixelY) {
        for (int i = 0; i < POINTS.length; i++) {
            double cx = toPixelX(POINTS[i][0]), cy = toPixelY(POINTS[i][1]);
            if (Math.hypot(pixelX - cx, pixelY - cy) <= POINT_R + POINT_SNAP) return i;
        }
        return -1;
    }

    /**
     * Метод для определения занята ли точка другим объектиком
     */
    private boolean slotOccupied(int pointIndex, Organization dragged) {
        double sx = POINTS[pointIndex][0], sy = POINTS[pointIndex][1];
        for (Organization o : orgs) {
            if (o.getId().equals(dragged.getId())) continue;
            Coordinates c = o.getCoordinates();
            if (Math.abs(c.getX() - sx) <= POINT_EPS && Math.abs(c.getY() - sy) <= POINT_EPS) return true;
        }
        return false;
    }

    private void rebuildObjects() {
        objectsLayer.getChildren().clear();
        circleById.clear();

        Set<Long> currentIds = new HashSet<>();
        for (Organization o : orgs) {
            if (o.getId() == null) continue;
            currentIds.add(o.getId());
            Circle c = makeCircle(o);
            circleById.put(o.getId(), c);
            objectsLayer.getChildren().add(c);

            if (!knownIds.contains(o.getId())) {
                playAppear(c); //анимация появления нового объекта на крутой яндекс карте
            }
        }
        knownIds.clear();
        knownIds.addAll(currentIds);
        requestLayout();
    }

    private Circle makeCircle(Organization o) {
        double r = radiusForTurnover(o.getAnnualTurnover());
        Circle c = new Circle(r);
        //цвет по владельцу, чтобы у каждого пользователя был свой невероятно уникальный цвет
        Color base = colorForOwner(o.getOwnerId());
        c.setFill(base.deriveColor(0, 1, 1, 0.85));

        boolean mine = myOwnerId != null && myOwnerId.equals(o.getOwnerId());
        c.setStroke(mine ? Color.web("#212529") : base.darker());
        c.setStrokeWidth(mine ? 3 : 1.5);

        Tooltip tip = new Tooltip(o.getName() + "  (ID " + o.getId() + ")\n"
                + lm.get("label.turnover") + ": " + o.getAnnualTurnover());
        Tooltip.install(c, tip);

        boolean draggable = canDrag.test(o);
        c.setCursor(draggable ? javafx.scene.Cursor.OPEN_HAND : javafx.scene.Cursor.HAND);
        installMouseHandlers(c, o, draggable);
        return c;
    }

    private void installMouseHandlers(Circle c, Organization o, boolean draggable) {
        final double[] press = new double[4]; // pressSceneX, pressSceneY, startCx, startCy

        c.setOnMousePressed(e -> {
            press[0] = e.getSceneX();
            press[1] = e.getSceneY();
            press[2] = c.getCenterX();
            press[3] = c.getCenterY();
            if (draggable) c.setCursor(javafx.scene.Cursor.CLOSED_HAND);
            c.toFront();
            e.consume();
        });

        c.setOnMouseDragged(e -> {
            if (!draggable) return;
            c.getProperties().put("dragging", Boolean.TRUE);
            double nx = press[2] + (e.getSceneX() - press[0]);
            double ny = press[3] + (e.getSceneY() - press[1]);
            nx = Math.max(0, Math.min(getWidth(), nx));
            ny = Math.max(0, Math.min(getHeight(), ny));
            c.setCenterX(nx);
            c.setCenterY(ny);
            e.consume();
        });

        c.setOnMouseReleased(e -> {
            boolean wasDragging = Boolean.TRUE.equals(c.getProperties().get("dragging"));
            c.getProperties().put("dragging", Boolean.FALSE);
            if (draggable) c.setCursor(javafx.scene.Cursor.OPEN_HAND);

            double moved = Math.hypot(e.getSceneX() - press[0], e.getSceneY() - press[1]);
            if (!wasDragging || moved < 5) {
                //это клик, а не перетаскивание, поэтому показываем информацию или допускаем к редактированию
                onObjectClicked.accept(o);
                e.consume();
                return;
            }

            double cx = c.getCenterX(), cy = c.getCenterY();
            int point = slotAt(cx, cy);
            float newX;
            long newY;
            if (point >= 0) {
                if (slotOccupied(point, o)) {
                    showMessage.accept(lm.get("msg.dropOccupied"));
                    snapBack(c, o);
                    e.consume();
                    return;
                }
                newX = (float) POINTS[point][0];
                newY = Math.round(POINTS[point][1]);
                c.setCenterX(toPixelX(newX));
                c.setCenterY(toPixelY(newY));
            } else {
                newX = toLogicalX(cx);
                newY = toLogicalY(cy);
            }
            onMoveCommitted.commit(o, newX, newY);
            e.consume();
        });
    }

    private void snapBack(Circle c, Organization o) {
        c.setCenterX(toPixelX(o.getCoordinates().getX()));
        c.setCenterY(toPixelY(o.getCoordinates().getY()));
    }

    private void playAppear(Circle c) {
        c.setOpacity(0);
        ScaleTransition st = new ScaleTransition(Duration.millis(420), c);
        st.setFromX(0.1);
        st.setFromY(0.1);
        st.setToX(1);
        st.setToY(1);
        FadeTransition ft = new FadeTransition(Duration.millis(420), c);
        ft.setFromValue(0);
        ft.setToValue(1);
        st.play();
        ft.play();
    }

    /**
     * Метод, позволяющий установить размер кружка по полю annualTurnover
     * (тут я ещё уменьшаю размер, чтобы совсем больших кругов не было на карте)
     */
    private double radiusForTurnover(Integer turnover) {
        if (turnover == null || turnover == 0) return MIN_R;
        double r = MIN_R + 4.5 * Math.log10(Math.abs((double) turnover));
        return Math.max(MIN_R, Math.min(MAX_R, r));
    }

    /**
     * Метод, позволяющий установить каждому пользователю свой оттенок для кружка
     */
    private Color colorForOwner(Long ownerId) {
        long id = (ownerId == null) ? 0 : ownerId;
        double hue = (id * 47) % 360;
        return Color.hsb(hue, 0.62, 0.85);
    }

    /**
     * Метод, предназначенный для построения легенды карты (там просто про цвета будет сказано)
     */
    private void buildLegend() {
        legendLayer.getChildren().clear();
        double pad = 10, lineH = 20, w = 160;
        int lines = 3;
        Rectangle bg = new Rectangle(w, pad * 2 + lineH * lines);
        bg.setArcWidth(12);
        bg.setArcHeight(12);
        bg.setFill(Color.web("#ffffff", 0.92));
        bg.setStroke(Color.web("#ced4da"));
        legendLayer.getChildren().add(bg);

        Text title = new Text(pad, pad + 12, lm.get("map.legend"));
        title.setFont(Font.font("System", FontWeight.BOLD, 12));
        legendLayer.getChildren().add(title);

        Circle mine = new Circle(pad + 6, pad + lineH + 6, 6, colorForOwner(myOwnerId));
        mine.setStroke(Color.web("#212529"));
        mine.setStrokeWidth(3);
        Text mineT = new Text(pad + 20, pad + lineH + 10, lm.get("map.you"));
        mineT.setFont(Font.font("System", 11));

        Circle other = new Circle(pad + 6, pad + lineH * 2 + 6, 6, Color.web("#adb5bd"));
        other.setStroke(Color.web("#868e96"));
        other.setStrokeWidth(1.5);
        Text otherT = new Text(pad + 20, pad + lineH * 2 + 10, lm.get("map.others"));
        otherT.setFont(Font.font("System", 11));

        legendLayer.getChildren().addAll(mine, mineT, other, otherT);
    }
}