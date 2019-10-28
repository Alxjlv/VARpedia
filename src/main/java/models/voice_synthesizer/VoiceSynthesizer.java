package models.voice_synthesizer;

import main.ProcessRunner;

import java.io.Externalizable;
import java.io.File;

/**
 * VoiceSynthesizer specifies an interface for concrete VoiceSynthesizer's to implement. VoiceSynthesizer extends
 * Externalizable allowing VoiceSynthesizer objects to be Serialized.
 * @author Tait & Alex
 */
public interface VoiceSynthesizer extends Externalizable {

    /**
     * Audibly plays the specified text as speech
     * @param text The text to be spoken
     */
    ProcessRunner preview(String text);

    /**
     * Saves the specified text as speech to the specified audio file
     * @param text The text to be saved
     * @param folder The folder to save audio.wav to
     * @return The saved audio File
     */
    File save(String text, File folder);

    /* Require VoiceSynthesizers to specify a toString() method */
    @Override
    String toString();
}
