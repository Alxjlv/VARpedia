package constants;

public enum View {
    WELCOME("/WelcomeView.fxml"),
    SEARCH("/SearchView.fxml"),
    VIDEO("/VideoView.fxml"),
    NAME("/NameView.fxml"),
    CHUNK("/ChunkView.fxml");

    private String scene;

    View(String scene) {
        this.scene = scene;
    }

    public String getScene() {
        return scene;
    }
}
