package client.ui;

import client.connection.Client;
import client.ui.dialogs.AddElementDialog;
import client.ui.dialogs.EditElementDialog;
import client.utility.LanguageManager;
import common.model.Address;
import common.model.Coordinates;
import common.model.Organization;
import common.transport.Request;
import common.transport.Response;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс {@code MainController} представляет собой контроллер главного окошка
 *
 * @author mikhailkramar
 */
public class MainController {

    @FXML private Label userInfoLabel;
    @FXML private Button logoutButton;
    @FXML private MenuButton langButton;

    @FXML private TabPane mainTabPane;
    @FXML private Tab tableTab;
    @FXML private Tab visualizationTab;
    @FXML private Tab consoleTab;

    @FXML private Button infoButton, addButton;
    @FXML private MenuButton moreButton;

    @FXML private TableView<Organization> tableView;
    @FXML private TableColumn<Organization, String> idColumn, nameColumn, xCoordinateColumn, yCoordinateColumn,
            creationDateColumn, annualTurnoverColumn, fullNameColumn, typeColumn, addressColumn, ownerIdColumn;
    @FXML private Label idSortLabel, nameSortLabel, xSortLabel, ySortLabel, dateSortLabel,
            turnoverSortLabel, fullNameSortLabel, typeSortLabel, addressSortLabel, ownerSortLabel;
    @FXML private TextField idFilter, nameFilter, xFilter, yFilter, dateFilter,
            turnoverFilter, fullNameFilter, typeFilter, addressFilter, ownerFilter;
    @FXML private Label footerLabel;

    @FXML private StackPane mapContainer;

    @FXML private TextArea consoleOutput;
    @FXML private TextField consoleInput;
    @FXML private Label consolePromptLabel;

    private final LanguageManager lm = LanguageManager.getInstance();
    private Client client;
    private String userLogin;
    private String userPassword;

    private final ObservableList<Organization> masterData = FXCollections.observableArrayList();
    private final ObservableList<Organization> tableItems = FXCollections.observableArrayList();

    private CityMapPane mapPane;
    private GuiConsole guiConsole;

    private ScheduledExecutorService poller;
    private final AtomicBoolean pollGuard = new AtomicBoolean(false);
    private String lastSignature = "";

    private Long myOwnerId;
    private String pendingOwnFullName;

    private NumberFormat numberFormat;
    private DateTimeFormatter dateFormatter;

    private final List<ColumnSpec> specs = new ArrayList<>();
    private ColumnSpec currentSort;
    private int sortState = 0;

    /**
     * Описание одной колонки, а именно как её отображать, фильтровать и сортировать
     */
    private static final class ColumnSpec {
        final TableColumn<Organization, String> column;
        final Label sortLabel;
        final TextField filter;
        final String headerKey;
        final Function<Organization, String> text;
        final Comparator<Organization> comparator;

        ColumnSpec(TableColumn<Organization, String> column, Label sortLabel, TextField filter,
                   String headerKey, Function<Organization, String> text, Comparator<Organization> comparator) {
            this.column = column;
            this.sortLabel = sortLabel;
            this.filter = filter;
            this.headerKey = headerKey;
            this.text = text;
            this.comparator = comparator;
        }
    }

    @FXML
    public void initialize() {
        rebuildFormatters();
        tableView.setItems(tableItems);
        buildColumns();
        buildLanguageMenu();

        tableView.setRowFactory(tv -> {
            TableRow<Organization> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    openEditDialog(row.getItem());
                }
            });
            return row;
        });

        langButton.setText(labelForLocale(lm.getCurrentLocale()));
        applyBundle();
    }

    /**
     * Метод, который принимает данные авторизованного пользователя, создаёт карту и запускает обновление
     * @param client объект класса Client для отправки запросов и получения ответов
     * @param userLogin логин
     * @param userPassword пароль
     * @param ownerId ID пользователя в нашей БД
     */
    public void initData(Client client, String userLogin, String userPassword, Long ownerId) {
        this.client = client;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.myOwnerId = ownerId;

        userInfoLabel.setText(lm.get("label.userInfo") + ": " + userLogin);

        mapPane = new CityMapPane();
        mapPane.setCallbacks(this::openEditDialog, this::onMapMove, this::showInfo, this::isOwn);
        mapContainer.getChildren().setAll(mapPane);

        guiConsole = new GuiConsole(client, userLogin, userPassword, consoleOutput,
                this::handleLogout, this::pollNow);
        guiConsole.start();

        fetchAndApply();
        startPolling();
    }

    /**
     * Метод, отвечающий за сортировку и фильтрацию колонок при помощи стримов
     */
    private void buildColumns() {
        specs.clear();
        specs.add(new ColumnSpec(idColumn, idSortLabel, idFilter, "label.id",
                o -> o.getId() == null ? "" : String.valueOf(o.getId()),
                Comparator.comparing(Organization::getId, Comparator.nullsLast(Comparator.naturalOrder()))));
        specs.add(new ColumnSpec(nameColumn, nameSortLabel, nameFilter, "label.name",
                o -> nz(o.getName()),
                Comparator.comparing(o -> nz(o.getName()).toLowerCase())));
        specs.add(new ColumnSpec(xCoordinateColumn, xSortLabel, xFilter, "label.xCoordinate",
                o -> o.getCoordinates() == null ? "" : numberFormat.format(o.getCoordinates().getX()),
                Comparator.comparingDouble(o -> o.getCoordinates() == null ? Double.NEGATIVE_INFINITY : o.getCoordinates().getX())));
        specs.add(new ColumnSpec(yCoordinateColumn, ySortLabel, yFilter, "label.yCoordinate",
                o -> (o.getCoordinates() == null || o.getCoordinates().getY() == null) ? "" : numberFormat.format(o.getCoordinates().getY()),
                Comparator.comparingLong(o -> (o.getCoordinates() == null || o.getCoordinates().getY() == null) ? Long.MIN_VALUE : o.getCoordinates().getY())));
        specs.add(new ColumnSpec(creationDateColumn, dateSortLabel, dateFilter, "label.creationDate",
                o -> o.getCreationDate() == null ? "" : o.getCreationDate().format(dateFormatter),
                Comparator.comparing(Organization::getCreationDate, Comparator.nullsLast(Comparator.naturalOrder()))));
        specs.add(new ColumnSpec(annualTurnoverColumn, turnoverSortLabel, turnoverFilter, "label.turnover",
                o -> o.getAnnualTurnover() == null ? "" : numberFormat.format(o.getAnnualTurnover()),
                Comparator.comparingInt(o -> o.getAnnualTurnover() == null ? Integer.MIN_VALUE : o.getAnnualTurnover())));
        specs.add(new ColumnSpec(fullNameColumn, fullNameSortLabel, fullNameFilter, "label.fullName",
                o -> nz(o.getFullName()),
                Comparator.comparing(o -> nz(o.getFullName()).toLowerCase())));
        specs.add(new ColumnSpec(typeColumn, typeSortLabel, typeFilter, "label.type",
                o -> o.getType() == null ? "—" : o.getType().name(),
                Comparator.comparing(o -> o.getType() == null ? "" : o.getType().name())));
        specs.add(new ColumnSpec(addressColumn, addressSortLabel, addressFilter, "label.address",
                o -> o.getOfficialAddress() == null ? "" : nz(o.getOfficialAddress().getStreet()),
                Comparator.comparing(o -> o.getOfficialAddress() == null ? "" : nz(o.getOfficialAddress().getStreet()).toLowerCase())));
        specs.add(new ColumnSpec(ownerIdColumn, ownerSortLabel, ownerFilter, "label.ownerId",
                o -> o.getOwnerId() == null ? "" : String.valueOf(o.getOwnerId()),
                Comparator.comparing(Organization::getOwnerId, Comparator.nullsLast(Comparator.naturalOrder()))));

        for (ColumnSpec s : specs) {
            s.column.setSortable(false);
            s.column.setCellValueFactory(cd -> {
                Organization o = cd.getValue();
                String value;
                try {
                    value = (o == null) ? "" : nz(s.text.apply(o));
                } catch (Exception ex) {
                    value = "";
                }
                return new ReadOnlyStringWrapper(value);
            });
            s.sortLabel.setOnMouseClicked(e -> handleSort(s));
            s.filter.textProperty().addListener((obs, o, n) -> refreshView());
        }
    }

    /**
     * Метод для обработки клика по шапке таблицы
     * @param spec
     */
    private void handleSort(ColumnSpec spec) {
        if (currentSort == spec) {
            sortState = (sortState + 1) % 3;
            if (sortState == 0) {
                currentSort = null;
            }
        } else {
            currentSort = spec;
            sortState = 1;
        }
        updateSortIndicators();
        refreshView();
    }

    /**
     * Метод для красивого оформления при нажатии (стрелочки спавнятся)
     */
    private void updateSortIndicators() {
        for (ColumnSpec s : specs) {
            String base = lm.get(s.headerKey);
            if (currentSort == s && sortState == 1) {
                s.sortLabel.setText(base + "  \u2191");
            } else if (currentSort == s && sortState == 2) {
                s.sortLabel.setText(base + "  \u2193");
            } else {
                s.sortLabel.setText(base);
            }
        }
    }

    /**
     * Метод, который пересобирает отображаемый список (фильтрует, сортирует)
     */
    private void refreshView() {
        Long selectedId = null;
        Organization sel = tableView.getSelectionModel().getSelectedItem();
        if (sel != null) {
            selectedId = sel.getId();
        }

        List<ColumnSpec> active = specs.stream()
                .filter(s -> s.filter.getText() != null && !s.filter.getText().trim().isEmpty())
                .collect(Collectors.toList());

        Stream<Organization> stream = masterData.stream();

        if (!active.isEmpty()) {
            stream = stream.filter(o -> active.stream().allMatch(s ->
                    s.text.apply(o).toLowerCase().contains(s.filter.getText().trim().toLowerCase())));
        }

        if (currentSort != null && sortState != 0) {
            Comparator<Organization> cmp = (sortState == 2) ? currentSort.comparator.reversed() : currentSort.comparator;
            stream = stream.sorted(cmp);
        }

        List<Organization> result = stream.collect(Collectors.toList());
        tableItems.setAll(result);

        if (selectedId != null) {
            for (Organization o : tableItems) {
                if (o.getId() != null && o.getId().equals(selectedId)) {
                    tableView.getSelectionModel().select(o);
                    break;
                }
            }
        }
    }

    /**
     * Метод, отвечающий за настройку локализации
     */
    private void rebuildFormatters() {
        Locale loc = lm.getCurrentLocale();
        numberFormat = NumberFormat.getNumberInstance(loc);
        numberFormat.setGroupingUsed(true);
        dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(loc);
    }

    private void buildLanguageMenu() {
        addLanguageItem("RU", new Locale("ru"));
        addLanguageItem("EN(GB)", new Locale("en", "GB"));
        addLanguageItem("SR", new Locale("sr"));
        addLanguageItem("BG", new Locale("bg"));
    }

    private void addLanguageItem(String label, Locale locale) {
        MenuItem item = new MenuItem(label);
        item.setOnAction(e -> {
            langButton.setText(label);
            lm.setLocale(locale);
            applyBundle();
        });
        langButton.getItems().add(item);
    }

    private String labelForLocale(Locale loc) {
        if (loc == null) return "RU";
        String lang = loc.getLanguage();
        if ("en".equals(lang)) return "EN(GB)";
        if ("sr".equals(lang)) return "SR";
        if ("bg".equals(lang)) return "BG";
        return "RU";
    }

    /**
     * Метод, который применяет переводы ко всем элементам окна и переформатирует всю таблицу в соответствии с языком
     */
    private void applyBundle() {
        rebuildFormatters();
        if (userLogin != null) {
            userInfoLabel.setText(lm.get("label.userInfo") + ": " + userLogin);
        }
        logoutButton.setText(lm.get("button.logout"));
        tableTab.setText(lm.get("tab.table"));
        visualizationTab.setText(lm.get("tab.visualization"));
        consoleTab.setText(lm.get("tab.console"));
        infoButton.setText(lm.get("button.info"));
        addButton.setText(lm.get("button.add"));
        moreButton.setText(lm.get("menu.button.more"));
        buildMoreMenu();
        tableView.setPlaceholder(new Label(lm.get("table.placeholder")));
        footerLabel.setText(lm.get("footer.text"));
        consoleInput.setPromptText(lm.get("console.prompt"));
        for (ColumnSpec s : specs) {
            s.filter.setPromptText(lm.get("prompt.filter"));
        }
        updateSortIndicators();
        if (mapPane != null) {
            mapPane.localize();
        }
        refreshView();
    }

    @FXML
    private void handleLogout() {
        stopPolling();
        if (guiConsole != null) guiConsole.stop();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setClient(client);

            Stage stage = window();
            stage.setTitle(lm.get("window.login.title"));
            stage.setScene(new Scene(root, 500, 520));
            stage.setResizable(false);
        } catch (Exception ex) {
            showError("Ошибка при возврате к окну входа: " + ex.getMessage());
        }
    }

    @FXML
    private void handleInfo() {
        sendAsync(req("info"), r -> showLongMessage(lm.get("button.info"),
                r == null ? lm.get("msg.serverUnavailable") : r.getMessage()));
    }

    @FXML
    private void handleAdd() {
        AddElementDialog dialog = new AddElementDialog(client, userLogin, userPassword, false);
        Optional<Organization> result = dialog.showAndWait();
        result.ifPresent(org -> addOrganization(org, false));
    }

    private void handleAddIfMin() {
        AddElementDialog dialog = new AddElementDialog(client, userLogin, userPassword, true);
        Optional<Organization> result = dialog.showAndWait();
        result.ifPresent(org -> addOrganization(org, true));
    }

    /**
     * Метод для крутейшего меню Дополнительно
     */
    private void buildMoreMenu() {
        moreButton.getItems().clear();

        MenuItem head = new MenuItem(lm.get("more.head"));
        head.setOnAction(e -> sendAsync(req("head"), r -> showResponseInfo(r)));

        MenuItem removeFirst = new MenuItem(lm.get("more.removeFirst"));
        removeFirst.setOnAction(e -> sendAsync(req("remove_first"), r -> {
            showResponseInfo(r);
            pollNow();
        }));

        MenuItem addIfMin = new MenuItem(lm.get("more.addIfMin"));
        addIfMin.setOnAction(e -> handleAddIfMin());

        MenuItem filterTurnover = new MenuItem(lm.get("more.filterLessTurnover"));
        filterTurnover.setOnAction(e -> {
            String arg = askNumber(lm.get("dialog.filterTurnover.header"));
            if (arg != null) sendAsync(reqArg("filter_less_than_annual_turnover", arg),
                    r -> showLongMessage(lm.get("more.filterLessTurnover"), responseText(r)));
        });

        MenuItem printAscending = new MenuItem(lm.get("more.printAscending"));
        printAscending.setOnAction(e -> sendAsync(req("print_field_ascending_annual_turnover"),
                r -> showLongMessage(lm.get("more.printAscending"), responseText(r))));

        MenuItem printUnique = new MenuItem(lm.get("more.printUniqueAddress"));
        printUnique.setOnAction(e -> sendAsync(req("print_unique_official_address"),
                r -> showLongMessage(lm.get("more.printUniqueAddress"), responseText(r))));

        MenuItem removeById = new MenuItem(lm.get("more.removeById"));
        removeById.setOnAction(e -> {
            String arg = askNumber(lm.get("dialog.removeById.header"));
            if (arg != null) sendAsync(reqArg("remove_by_id", arg), r -> {
                showResponseInfo(r);
                pollNow();
            });
        });

        moreButton.getItems().addAll(head, removeFirst, addIfMin, filterTurnover,
                printAscending, printUnique, removeById);
    }

    /**
     * Метод для текстового представления ответа (сообщение и элементы коллекции)
     * */
    private String responseText(Response r) {
        if (r == null) return lm.get("msg.serverUnavailable");
        StringBuilder sb = new StringBuilder(nz(r.getMessage()));
        if (r.getCollection() != null) {
            for (Object o : r.getCollection()) sb.append('\n').append(o);
        }
        return sb.toString();
    }

    @FXML
    private void handleConsoleInput() {
        String line = consoleInput.getText() == null ? "" : consoleInput.getText().trim();
        consoleInput.clear();
        if (line.isEmpty()) return;
        if (guiConsole != null) {
            guiConsole.submit(line);
        }
    }

    private void addOrganization(Organization org, boolean addIfMin) {
        pendingOwnFullName = org.getFullName();
        Request req = new Request(userLogin, userPassword, addIfMin ? "add_if_min" : "add", null, org);
        sendAsync(req, r -> {
            showResponseInfo(r);
            pollNow();
        });
    }

    private void openEditDialog(Organization org) {
        boolean owner = isOwn(org);
        EditElementDialog dialog = new EditElementDialog(client, userLogin, userPassword, org, owner);
        Optional<EditElementDialog.Result> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        EditElementDialog.Result res = result.get();
        if (res.action == EditElementDialog.Action.SAVE) {
            updateOrganization(org, res.organization);
        } else if (res.action == EditElementDialog.Action.DELETE) {
            deleteOrganization(org);
        }
    }

    private void updateOrganization(Organization original, Organization newOrg) {
        Request req = new Request(userLogin, userPassword, "update", String.valueOf(original.getId()), newOrg);
        sendAsync(req, r -> {
            if (r != null && r.isSuccess() && original.getOwnerId() != null) {
                myOwnerId = original.getOwnerId();
            }
            showResponseInfo(r);
            pollNow();
        });
    }

    private void deleteOrganization(Organization org) {
        Request req = new Request(userLogin, userPassword, "remove_by_id", String.valueOf(org.getId()), null);
        sendAsync(req, r -> {
            if (r != null && r.isSuccess() && org.getOwnerId() != null) {
                myOwnerId = org.getOwnerId();
            }
            showResponseInfo(r);
            pollNow();
        });
    }

    /**
     * Метод для перетаскивания объекта на карте (меняем только координаты)
     */
    private void onMapMove(Organization org, float newX, long newY) {
        if (myOwnerId != null && !isOwn(org)) {
            showInfo(lm.get("msg.notOwner"));
            redrawMap();
            return;
        }

        if (!common.utility.Validator.validateX(newX) || !common.utility.Validator.validateY(newY)) {
            showInfo(lm.get("msg.badCoordinates"));
            redrawMap();
            return;
        }

        Organization moved = new Organization(org.getName(), new Coordinates(newX, newY),
                org.getAnnualTurnover(), org.getFullName(), org.getType(), org.getOfficialAddress());
        Request req = new Request(userLogin, userPassword, "update", String.valueOf(org.getId()), moved);
        sendAsync(req, r -> {
            if (r != null && r.isSuccess()) {
                if (org.getOwnerId() != null) myOwnerId = org.getOwnerId();
                appendConsole(lm.get("msg.moved"));
                pollNow();
            } else {
                showInfo(r == null ? lm.get("msg.serverUnavailable")
                        : (isOwn(org) ? r.getMessage() : lm.get("msg.notOwner")));
                redrawMap();
            }
        });
    }

    /**
     * Метод для перерисовывания карты из текущих данных (использую для отката перетаскивания)
     */
    private void redrawMap() {
        if (mapPane != null) {
            mapPane.setData(new ArrayList<>(masterData), myOwnerId);
        }
    }

    private boolean isOwn(Organization org) {
        if (myOwnerId == null) return true;
        return myOwnerId.equals(org.getOwnerId());
    }

    private void startPolling() {
        poller = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "collection-poller");
            t.setDaemon(true);
            return t;
        });
        poller.scheduleAtFixedRate(this::fetchAndApply, 2, 2, TimeUnit.SECONDS);
    }

    private void stopPolling() {
        if (poller != null) {
            poller.shutdownNow();
            poller = null;
        }
    }

    private void pollNow() {
        fetchAndApply();
    }

    private void fetchAndApply() {
        if (!pollGuard.compareAndSet(false, true)) return;
        Thread t = new Thread(() -> {
            try {
                Response r = client.sendAndReceive(new Request(userLogin, userPassword, "show", null, null));
                List<Organization> data = extract(r);
                Platform.runLater(() -> applyData(data));
            } finally {
                pollGuard.set(false);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private List<Organization> extract(Response r) {
        if (r == null) return null;
        List<Organization> out = new ArrayList<>();
        Collection<?> c = r.getCollection();
        if (c != null) {
            for (Object o : c) {
                if (o instanceof Organization org) out.add(org);
            }
        }
        return out;
    }

    private void applyData(List<Organization> data) {
        if (data == null) return;

        String sig = signature(data);
        if (sig.equals(lastSignature)) return;
        lastSignature = sig;

        masterData.setAll(data);

        if (myOwnerId == null && pendingOwnFullName != null) {
            for (Organization o : data) {
                if (pendingOwnFullName.equals(o.getFullName())) {
                    myOwnerId = o.getOwnerId();
                    break;
                }
            }
            if (myOwnerId != null) pendingOwnFullName = null;
        }

        refreshView();
        if (mapPane != null) {
            mapPane.setData(new ArrayList<>(masterData), myOwnerId);
        }
    }

    private String signature(List<Organization> data) {
        StringBuilder sb = new StringBuilder();
        for (Organization o : data) {
            sb.append(o.getId()).append('|')
                    .append(o.getCoordinates().getX()).append('|')
                    .append(o.getCoordinates().getY()).append('|')
                    .append(o.getName()).append('|')
                    .append(o.getAnnualTurnover()).append('|')
                    .append(o.getFullName()).append('|')
                    .append(o.getType()).append('|')
                    .append(o.getOfficialAddress() == null ? "" : o.getOfficialAddress().getStreet()).append('|')
                    .append(o.getOwnerId()).append(';');
        }
        return sb.toString();
    }

    /**
     * Метод для настройки прямого вывода сообщений в консоль
     * @param s строка
     */
    private void appendConsole(String s) {
        if (Platform.isFxApplicationThread()) {
            consoleOutput.appendText(s + "\n");
        } else {
            Platform.runLater(() -> consoleOutput.appendText(s + "\n"));
        }
    }

    /**
     * Вспомогательный метод для синхронизации всего между клиентами
     * @param req объект запроса
     * @param onFx функция обратного вызова (она выполнится сама после получения ответа от сервера)
     */
    private void sendAsync(Request req, java.util.function.Consumer<Response> onFx) {
        Thread t = new Thread(() -> {
            Response r = client.sendAndReceive(req);
            Platform.runLater(() -> onFx.accept(r));
        });
        t.setDaemon(true);
        t.start();
    }

    private Request req(String name) {
        return new Request(userLogin, userPassword, name, null, null);
    }

    private Request reqArg(String name, String arg) {
        return new Request(userLogin, userPassword, name, arg, null);
    }

    /**
     * Метод, запрашивающий число у пользователя
     * (сделал тупо для команд, которые запрашивают ввод числа)
     */
    private String askNumber(String header) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(lm.get("dialog.info.title"));
        dialog.setHeaderText(header);
        dialog.getEditor().setPromptText(lm.get("dialog.id.prompt"));
        Optional<String> res = dialog.showAndWait();
        if (res.isEmpty()) return null;
        String v = res.get().trim();
        try {
            Long.parseLong(v);
            return v;
        } catch (NumberFormatException e) {
            showError(lm.get("valid.number"));
            return null;
        }
    }

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(lm.get("alert.confirm.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> r = alert.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(lm.get("alert.info.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(lm.get("alert.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showResponseInfo(Response r) {
        if (r == null) {
            showError(lm.get("msg.serverUnavailable"));
            return;
        }
        Alert alert = new Alert(r.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
        alert.setTitle(r.isSuccess() ? lm.get("alert.info.title") : lm.get("alert.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(r.getMessage());
        alert.showAndWait();
    }

    private void showLongMessage(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        TextArea area = new TextArea(text);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefSize(540, 380);
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    private Stage window() {
        return (Stage) tableView.getScene().getWindow();
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
