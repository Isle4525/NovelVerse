package com.novelverse.novelverse.desktop;

import com.novelverse.novelverse.NovelverseApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NovelVerseDesktopApp extends Application {

    private static final String BACKEND_URL = "http://127.0.0.1:8080";
    private static final int DESKTOP_PROXY_PORT = 18080;

    private ConfigurableApplicationContext springContext;
    private ExecutorService backgroundExecutor;
    private DesktopSocketChatClient chatClient;
    private DesktopWebProxyServer proxyServer;
    private WebEngine webEngine;
    private Label browserStatusLabel;
    private Label appStatusLabel;
    private Label chatStatusLabel;
    private ListView<String> messagesView;
    private TextField chatNameField;
    private TextField chatInputField;
    private Button connectButton;
    private Button disconnectButton;
    private Button sendButton;
    private Tab chatTab;
    private double dragOffsetX;
    private double dragOffsetY;
    private boolean maximized;
    private double savedX;
    private double savedY;
    private double savedWidth;
    private double savedHeight;

    @Override
    public void init() {
        backgroundExecutor = Executors.newCachedThreadPool();
        springContext = SpringApplication.run(NovelverseApplication.class);
        chatClient = new DesktopSocketChatClient("127.0.0.1", 9090);
        proxyServer = new DesktopWebProxyServer(BACKEND_URL, DESKTOP_PROXY_PORT, backgroundExecutor);
        try {
            proxyServer.start();
        } catch (Exception e) {
            throw new RuntimeException("Unable to start desktop proxy", e);
        }
    }

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.UNDECORATED);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle(
                "-fx-background-color: #0d0d14;" +
                "-fx-tab-min-width: 150;" +
                "-fx-tab-max-width: 220;"
        );

        Tab browserTab = new Tab("Library");
        browserTab.setContent(buildBrowserPane(tabPane));

        chatTab = new Tab("Socket Chat");
        chatTab.setContent(buildChatPane());

        tabPane.getTabs().addAll(browserTab, chatTab);

        Label titleLabel = new Label("NovelVerse Desktop");
        titleLabel.setTextFill(Color.web("#ff4d7a"));
        titleLabel.setFont(Font.font("Segoe UI", 24));
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label subtitleLabel = new Label("Desktop proxy on 18080, backend on 8080, socket chat on 9090.");
        subtitleLabel.setTextFill(Color.web("#b7b8c8"));

        appStatusLabel = new Label("Preparing desktop workspace...");
        appStatusLabel.setTextFill(Color.web("#9fa1b5"));

        VBox titleBox = new VBox(4, titleLabel, subtitleLabel, appStatusLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Button minimizeButton = createWindowButton("—", "#2d3147", () -> stage.setIconified(true));
        Button maximizeButton = createWindowButton("□", "#2d3147", () -> toggleMaximize(stage));
        Button closeButton = createWindowButton("✕", "#7a2438", stage::close);

        HBox windowButtons = new HBox(8, minimizeButton, maximizeButton, closeButton);
        windowButtons.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(18, titleBox, spacer, windowButtons);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 22, 14, 22));
        topBar.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(24,25,39,0.96), rgba(32,34,54,0.96));" +
                        "-fx-border-color: rgba(255,255,255,0.08);" +
                        "-fx-border-width: 0 0 1 0;"
        );
        enableWindowDragging(stage, topBar);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);
        root.setStyle("-fx-background-color: #0d0d14;");

        Scene scene = new Scene(root, 1440, 920);
        scene.setFill(Color.web("#0d0d14"));
        stage.setTitle("NovelVerse Desktop");
        stage.setMinWidth(1180);
        stage.setMinHeight(760);
        stage.setScene(scene);
        stage.show();

        loadSite();
    }

    @Override
    public void stop() {
        if (chatClient != null) {
            chatClient.disconnect();
        }
        if (proxyServer != null) {
            proxyServer.stop();
        }
        if (backgroundExecutor != null) {
            backgroundExecutor.shutdownNow();
        }
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }

    private BorderPane buildBrowserPane(TabPane tabPane) {
        WebView webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124 Safari/537.36");

        browserStatusLabel = new Label("Booting backend and checking database...");
        browserStatusLabel.setTextFill(Color.web("#d7d9e6"));
        browserStatusLabel.setWrapText(true);

        VBox navColumn = new VBox(10);
        navColumn.setPadding(new Insets(16));
        navColumn.setPrefWidth(235);
        navColumn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(24,25,39,0.98), rgba(19,20,30,0.98));" +
                        "-fx-border-color: rgba(255,255,255,0.07);" +
                        "-fx-border-width: 0 1 0 0;"
        );

        Label navTitle = new Label("Navigation");
        navTitle.setTextFill(Color.web("#f4f2f8"));
        navTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button catalogButton = createNavButton("Catalog", () -> webEngine.load(proxyServer.getBaseUrl() + "/index.html"));
        Button profileButton = createNavButton("Profile", () -> webEngine.load(proxyServer.getBaseUrl() + "/profile.html"));
        Button adminButton = createNavButton("Manage", () -> webEngine.load(proxyServer.getBaseUrl() + "/admin.html"));
        Button loginButton = createNavButton("Login", () -> webEngine.load(proxyServer.getBaseUrl() + "/login.html"));
        Button registerButton = createNavButton("Register", () -> webEngine.load(proxyServer.getBaseUrl() + "/register.html"));
        Button refreshButton = createNavButton("Refresh Page", webEngine::reload);
        Button openChatButton = createNavButton("Open Native Chat", () -> tabPane.getSelectionModel().select(chatTab));

        Label infoTitle = new Label("Desktop Proxy Mode");
        infoTitle.setTextFill(Color.web("#f4f2f8"));
        infoTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Label infoText = new Label(
                "WebView now talks to a local desktop proxy on port 18080. " +
                        "The proxy forwards requests to backend on 8080 and smooths over WebView compatibility issues."
        );
        infoText.setWrapText(true);
        infoText.setTextFill(Color.web("#a7a9ba"));

        navColumn.getChildren().addAll(
                navTitle,
                catalogButton,
                profileButton,
                adminButton,
                loginButton,
                registerButton,
                refreshButton,
                openChatButton,
                new Separator(),
                infoTitle,
                infoText,
                new Separator(),
                browserStatusLabel
        );

        StackPane webPane = new StackPane(webView);
        webPane.setPadding(new Insets(14));
        webPane.setStyle("-fx-background-color: #0d0d14;");

        BorderPane pane = new BorderPane();
        pane.setLeft(navColumn);
        pane.setCenter(webPane);
        pane.setStyle("-fx-background-color: #0d0d14;");

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                browserStatusLabel.setText("Page loaded from " + webEngine.getLocation());
                appStatusLabel.setText("Desktop app is ready.");
            } else if (newState == Worker.State.RUNNING) {
                browserStatusLabel.setText("Loading " + webEngine.getLocation());
                appStatusLabel.setText("Loading page in WebView...");
            } else if (newState == Worker.State.FAILED) {
                browserStatusLabel.setText("WebView failed to load page.");
                appStatusLabel.setText("Web UI needs attention.");
            }
        });

        return pane;
    }

    private VBox buildChatPane() {
        messagesView = new ListView<>();
        messagesView.setPlaceholder(new Label("Connect to the socket chat to start the discussion."));
        messagesView.setStyle(
                "-fx-control-inner-background: #141522;" +
                        "-fx-background-color: #141522;" +
                        "-fx-border-color: rgba(255,255,255,0.06);" +
                        "-fx-border-radius: 18;" +
                        "-fx-background-radius: 18;" +
                        "-fx-text-fill: #f4f2f8;"
        );

        chatNameField = new TextField("Reader");
        chatNameField.setPromptText("Chat name");

        chatInputField = new TextField();
        chatInputField.setPromptText("Write a message");
        chatInputField.setOnAction(event -> sendChatMessage());

        connectButton = createActionButton("Connect", "#ff4d7a", this::connectToChat);
        disconnectButton = createActionButton("Disconnect", "#3a3d53", this::disconnectFromChat);
        disconnectButton.setDisable(true);

        sendButton = createActionButton("Send", "#ff4d7a", this::sendChatMessage);
        sendButton.setDisable(true);

        chatStatusLabel = new Label("Socket chat is waiting on port 9090.");
        chatStatusLabel.setTextFill(Color.web("#c7c8d7"));
        chatStatusLabel.setWrapText(true);

        Label chatTitle = new Label("Native Socket Chat");
        chatTitle.setTextFill(Color.web("#f4f2f8"));
        chatTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label chatDescription = new Label(
                "This tab connects directly to the existing ServerSocket on port 9090."
        );
        chatDescription.setWrapText(true);
        chatDescription.setTextFill(Color.web("#9fa1b5"));

        HBox topRow = new HBox(10, chatNameField, connectButton, disconnectButton);
        HBox.setHgrow(chatNameField, Priority.ALWAYS);

        HBox bottomRow = new HBox(10, chatInputField, sendButton);
        HBox.setHgrow(chatInputField, Priority.ALWAYS);

        VBox pane = new VBox(14, chatTitle, chatDescription, topRow, chatStatusLabel, messagesView, bottomRow);
        pane.setPadding(new Insets(20));
        pane.setStyle("-fx-background-color: linear-gradient(to bottom, #10111a, #0d0d14);");
        VBox.setVgrow(messagesView, Priority.ALWAYS);

        return pane;
    }

    private Button createNavButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(12, 16, 12, 16));
        button.setStyle(
                "-fx-background-color: rgba(255,255,255,0.04);" +
                        "-fx-text-fill: #f4f2f8;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-color: rgba(255,255,255,0.08);"
        );
        button.setOnAction(event -> action.run());
        return button;
    }

    private Button createActionButton(String text, String color, Runnable action) {
        Button button = new Button(text);
        button.setPadding(new Insets(11, 16, 11, 16));
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 14;"
        );
        button.setOnAction(event -> action.run());
        return button;
    }

    private Button createWindowButton(String text, String color, Runnable action) {
        Button button = new Button(text);
        button.setMinSize(40, 30);
        button.setPrefSize(40, 30);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;"
        );
        button.setOnAction(event -> action.run());
        return button;
    }

    private void enableWindowDragging(Stage stage, HBox dragArea) {
        dragArea.setOnMousePressed(event -> {
            if (maximized) {
                return;
            }
            dragOffsetX = event.getSceneX();
            dragOffsetY = event.getSceneY();
        });

        dragArea.setOnMouseDragged(event -> {
            if (maximized) {
                return;
            }
            stage.setX(event.getScreenX() - dragOffsetX);
            stage.setY(event.getScreenY() - dragOffsetY);
        });

        dragArea.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                toggleMaximize(stage);
            }
        });
    }

    private void toggleMaximize(Stage stage) {
        if (!maximized) {
            savedX = stage.getX();
            savedY = stage.getY();
            savedWidth = stage.getWidth();
            savedHeight = stage.getHeight();

            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            maximized = true;
            return;
        }

        stage.setX(savedX);
        stage.setY(savedY);
        stage.setWidth(savedWidth);
        stage.setHeight(savedHeight);
        maximized = false;
    }

    private void loadSite() {
        backgroundExecutor.submit(() -> {
            for (int i = 0; i < 30; i++) {
                if (isBackendReady()) {
                    Platform.runLater(() -> webEngine.load(proxyServer.getBaseUrl() + "/index.html"));
                    return;
                }

                Platform.runLater(() -> {
                    appStatusLabel.setText("Waiting for backend on 8080...");
                    if (browserStatusLabel != null) {
                        browserStatusLabel.setText("Checking backend availability...");
                    }
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            Platform.runLater(() -> webEngine.loadContent("""
                    <html>
                    <body style="font-family:Segoe UI;padding:36px;background:#0d0d14;color:#f4f2f8;">
                    <h2>Backend is not ready</h2>
                    <p>Check that Spring Boot started on http://127.0.0.1:8080 and PostgreSQL is available for /novels.</p>
                    </body>
                    </html>
                    """));
        });
    }

    private boolean isBackendReady() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(BACKEND_URL + "/novels").openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            int code = connection.getResponseCode();
            return code >= 200 && code < 300;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void connectToChat() {
        String username = chatNameField.getText() == null || chatNameField.getText().isBlank()
                ? "Reader"
                : chatNameField.getText().trim();

        chatStatusLabel.setText("Connecting to socket chat on 9090...");
        appStatusLabel.setText("Opening native chat connection...");
        connectButton.setDisable(true);

        backgroundExecutor.submit(() -> {
            try {
                messagesView.getItems().clear();
                chatClient.connect(username, message -> Platform.runLater(() -> {
                    messagesView.getItems().add(message);
                    messagesView.scrollTo(messagesView.getItems().size() - 1);
                }));

                Platform.runLater(() -> {
                    chatStatusLabel.setText("Connected to 9090 as " + username);
                    appStatusLabel.setText("Chat is connected.");
                    disconnectButton.setDisable(false);
                    sendButton.setDisable(false);
                    chatNameField.setDisable(true);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    chatStatusLabel.setText("Connection error: " + e.getMessage());
                    appStatusLabel.setText("Chat connection failed.");
                    connectButton.setDisable(false);
                });
            }
        });
    }

    private void disconnectFromChat() {
        chatClient.disconnect();
        chatStatusLabel.setText("Disconnected from chat.");
        appStatusLabel.setText("Chat was disconnected.");
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);
        sendButton.setDisable(true);
        chatNameField.setDisable(false);
    }

    private void sendChatMessage() {
        String text = chatInputField.getText();
        if (text == null || text.isBlank()) {
            return;
        }

        try {
            chatClient.send(text.trim());
            chatInputField.clear();
        } catch (Exception e) {
            chatStatusLabel.setText("Send failed: " + e.getMessage());
            appStatusLabel.setText("Unable to send chat message.");
        }
    }
}
