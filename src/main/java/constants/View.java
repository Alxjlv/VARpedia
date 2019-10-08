package constants;

import java.net.URL;

public enum View {
    WELCOME("/WelcomeView.fxml"),
    SEARCH("/SearchView.fxml"),
    VIDEO("/VideoView.fxml"),
    NAME("/NameView.fxml"),
    CHUNK("/ChunkView.fxml");

    private URL scene;

    View(String scene) {
        this.scene = getClass().getResource(scene);
    }

    public URL getScene() {
        return scene;
    }
}
