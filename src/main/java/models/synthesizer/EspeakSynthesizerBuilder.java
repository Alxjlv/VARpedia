package models.synthesizer;

import models.Builder;

public class EspeakSynthesizerBuilder implements Builder<EspeakSynthesizer> {

    private EspeakSynthesizer.Voice voice;

    public EspeakSynthesizerBuilder() {
        this.voice = null;
    }

    public EspeakSynthesizerBuilder(EspeakSynthesizer synthesizer) {
        this.voice = synthesizer.getVoice();
    }

    public EspeakSynthesizerBuilder setVoice(EspeakSynthesizer.Voice voice) {
        this.voice = voice;
        return this;
    }

    @Override
    public EspeakSynthesizer build() {
        return new EspeakSynthesizer(voice);
    }
}
