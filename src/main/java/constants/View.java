package constants;

import java.net.URL;

public enum View {
    WELCOME("/WelcomeView.fxml"),
    SEARCH("/SearchView.fxml"),
    VIDEO("/VideoView.fxml"),
    NAME("/NameView.fxml"),
    CHUNK("/ChunkView.fxml"),
    CREATION_CELL("/CreationCell.fxml");

    private final URL scene;

    View(String scene) {
        this.scene = getClass().getResource(scene);
    }

    public URL get() {
        return scene;
    }
}
