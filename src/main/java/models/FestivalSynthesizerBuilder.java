package models;

public class FestivalSynthesizerBuilder implements Builder<FestivalSynthesizer> {

    private FestivalSynthesizer.Voice voice;

    public FestivalSynthesizerBuilder() {
        this.voice = null;
    }

    public FestivalSynthesizerBuilder(FestivalSynthesizer synthesizer) {
        this.voice = synthesizer.getVoice();
    }

    public FestivalSynthesizerBuilder setVoice(FestivalSynthesizer.Voice voice) {
        this.voice = voice;
    }

    @Override
    public FestivalSynthesizer build() {
        return new FestivalSynthesizer(voice);
    }
}
