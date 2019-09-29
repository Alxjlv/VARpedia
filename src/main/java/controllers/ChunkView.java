package controllers;

import events.SwitchSceneEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import models.*;
import views.ChunkCellFactory;

public class ChunkView extends Controller {

    @FXML ListView<Chunk> chunksListView;

    @FXML TextArea searchResult;

    private Synthesizer synthesizer;

    @FXML
    public void initialize() {
        ChunkManager.getInstance(); // TODO - This will clear chunks if user goes back and returns
        chunksListView.setItems(ChunkManager.getInstance().getItems());
        chunksListView.setCellFactory(new ChunkCellFactory());
        ChunkManager.getInstance().getItems().addListener(new ListChangeListener<Chunk>() {
            @Override
            public void onChanged(Change<? extends Chunk> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        chunksListView.getSelectionModel().select(c.getFrom());
                    }
                }
            }
        });

        synthesizer = new EspeakSynthesizerBuilder().setVoice(EspeakSynthesizer.Voice.DEFAULT).build();
        // TODO - Load Wikit Result
        searchResult.setText("An apple is a sweet, edible fruit produced by an apple tree (Malus domestica). Apple " +
                "trees are cultivated worldwide and are the most widely grown species in the genus Malus. The tree " +
                "originated in Central Asia, where its wild ancestor, Malus sieversii, is still found today. Apples " +
                "have been grown for thousands of years in Asia and Europe and were brought to North America by " +
                "European colonists. Apples have religious and mythological significance in many cultures, including" +
                " Norse, Greek and European Christian traditions."); // TODO - Remove
    }

    @FXML public void pressBack() {
        listener.handle(new SwitchSceneEvent(this, "/CreateView.fxml"));
        // TODO - Save TextArea searchResult?
    }

    @FXML public void pressSpeech() {
        System.out.println("Press speech");

        Stage synthesizerStage = new Stage();
        synthesizerStage.initModality(Modality.APPLICATION_MODAL);
        synthesizerStage.initOwner(Main.getPrimaryStage());

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                synthesizerStage.close();
            }
        });
        Button saveButton = new Button("Save");

        RadioButton selectEspeak = new RadioButton("Espeak");
        RadioButton selectFestival = new RadioButton("Festival");

        ToggleGroup selectSynthesizer = new ToggleGroup();
        selectEspeak.setToggleGroup(selectSynthesizer);
        selectFestival.setToggleGroup(selectSynthesizer);

        ChoiceBox voices = new ChoiceBox<>();
        if (synthesizer instanceof EspeakSynthesizer) {
            EspeakSynthesizerBuilder builder = new EspeakSynthesizerBuilder((EspeakSynthesizer) synthesizer);
            selectSynthesizer.selectToggle(selectEspeak);
            voices.getItems().setAll(EspeakSynthesizer.Voice.values());
            voices.getSelectionModel().select(((EspeakSynthesizer) synthesizer).getVoice());

            saveButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    synthesizer = builder.setVoice((EspeakSynthesizer.Voice) voices.getValue()).build();
                    System.out.println(synthesizer); // TODO - Remove
                    synthesizerStage.close();
                }
            });
        } else if (synthesizer instanceof FestivalSynthesizer) {
            FestivalSynthesizerBuilder builder = new FestivalSynthesizerBuilder((FestivalSynthesizer) synthesizer);
            selectSynthesizer.selectToggle(selectFestival);
            voices.getItems().setAll(FestivalSynthesizer.Voice.values());
            voices.getSelectionModel().select(((FestivalSynthesizer) synthesizer).getVoice());

            saveButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    synthesizer = builder.setVoice((FestivalSynthesizer.Voice) voices.getValue()).build();
                    System.out.println(synthesizer); // TODO - Remove
                    synthesizerStage.close();
                }
            });
        }

        selectSynthesizer.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (selectSynthesizer.getSelectedToggle() == selectEspeak) {
                    EspeakSynthesizerBuilder builder = new EspeakSynthesizerBuilder();
                    voices.getItems().setAll(EspeakSynthesizer.Voice.values());
                    voices.getSelectionModel().select(EspeakSynthesizer.Voice.DEFAULT);

                    saveButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            synthesizer = builder.setVoice((EspeakSynthesizer.Voice) voices.getValue()).build();
                            System.out.println(synthesizer); // TODO - Remove
                            synthesizerStage.close();
                        }
                    });
                } else if (selectSynthesizer.getSelectedToggle() == selectFestival) {
                    FestivalSynthesizerBuilder builder = new FestivalSynthesizerBuilder();
                    voices.getItems().setAll(FestivalSynthesizer.Voice.values());
                    voices.getSelectionModel().select(FestivalSynthesizer.Voice.KAL);

                    saveButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            synthesizer = builder.setVoice((FestivalSynthesizer.Voice) voices.getValue()).build();
                            System.out.println(synthesizer); // TODO - Remove
                            synthesizerStage.close();
                        }
                    });
                }
            }
        });

        VBox settings = new VBox();
        settings.getChildren().add(new Text("Settings Popup"));
        settings.getChildren().add(selectEspeak);
        settings.getChildren().add(selectFestival);
        settings.getChildren().add(voices);
        settings.getChildren().add(cancelButton);
        settings.getChildren().add(saveButton);

        Scene scene = new Scene(settings, 200,300);
        synthesizerStage.setScene(scene);
        synthesizerStage.show();
    }

    @FXML public void pressPreview() {
        // TODO - Word count validation (20-40)
        // TODO - add ability to stop preview (e.g. preview button becomes cancel/stop button)
        synthesizer.preview(searchResult.getSelectedText());
    }

    @FXML public void pressSaveSnippet() {
        // TODO - Word count validation (20-40)
        ChunkBuilder chunkBuilder = ChunkManager.getInstance().getBuilder();
        chunkBuilder.setText(searchResult.getSelectedText()).setSynthesizer(synthesizer);
        ChunkManager.getInstance().create(chunkBuilder);
    }

    @FXML public void pressPlayback() {
        // TODO - Turn this into Task and make synthesizer.preview() block while running allowing sequential (not simultaneous) playback
        for (Chunk chunk: chunksListView.getItems()) {
            chunksListView.getSelectionModel().select(chunk);
            synthesizer.preview(chunk.getText());
        }
    }

    @FXML public void pressPreviewSnippet() {
        // TODO - add ability to stop preview (e.g. preview button becomes cancel/stop button, click on a different snippet)
        synthesizer.preview(chunksListView.getSelectionModel().selectedItemProperty().getValue().getText());

        Media media = new Media(chunksListView.getSelectionModel().getSelectedItem().getAudioFile().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setAutoPlay(true);
//        player.onReadyProperty().addListener(new ChangeListener<Runnable>() {
//            @Override
//            public void changed(ObservableValue<? extends Runnable> observable, Runnable oldValue, Runnable newValue) {
//                player.play();
//            }
//        });
    }

    @FXML public void pressDelete() {
        // TODO - Display alert?
        ChunkManager.getInstance().delete(chunksListView.getSelectionModel().selectedItemProperty().getValue());
    }

    @FXML public void pressNext() {
        listener.handle(new SwitchSceneEvent(this,"/NameView.fxml"));
    }
}
