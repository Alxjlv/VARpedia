package models.synthesizer;

import models.Builder;

public class FestivalSynthesizerBuilder implements Builder<Synthesizer> {

    private FestivalSynthesizer.Voice voice;

    public FestivalSynthesizerBuilder() {
        this.voice = null;
    }

    public FestivalSynthesizerBuilder(FestivalSynthesizer synthesizer) {
        this.voice = synthesizer.getVoice();
    }

    public FestivalSynthesizerBuilder setVoice(FestivalSynthesizer.Voice voice) {
        this.voice = voice;
        return this;
    }

    @Override
    public FestivalSynthesizer build() {
        return new FestivalSynthesizer(voice);
    }
}
