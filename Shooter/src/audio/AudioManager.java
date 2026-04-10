package audio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.*;

public class AudioManager {

    private Clip musicClip;
    private Clip shootClip;

    private boolean musicEnabled = true;
    private boolean soundEnabled = true;
    private float masterVolume   = 0.75f; // 0.0 – 1.0

    public AudioManager() {
        loadMusic();
        loadShootSound();
    }

    private void loadMusic() {
        // Try to load the MP3 - may fail without a third-party SPI
        try {
            URL url = AudioManager.class.getResource("/Acid_Network.wav");
            if (url == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            AudioFormat base  = ais.getFormat();
            AudioFormat target = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                base.getSampleRate(), 16,
                base.getChannels(), base.getChannels() * 2,
                base.getSampleRate(), false);
            AudioInputStream converted = AudioSystem.getAudioInputStream(target, ais);
            musicClip = AudioSystem.getClip();
            musicClip.open(converted);
            setVolume(musicClip, masterVolume);
        } catch (Exception e) {
            System.out.println("[Audio] Music not loaded (MP3 SPI missing?): " + e.getMessage());
        }
    }

    private void loadShootSound() {
        try {
            URL url = AudioManager.class.getResource("/Blaster.wav");
            if (url == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            shootClip = AudioSystem.getClip();
            shootClip.open(ais);
            setVolume(shootClip, masterVolume);
        } catch (Exception e) {
            System.out.println("[Audio] Shoot sound not loaded: " + e.getMessage());
        }
    }

    private void setVolume(Clip clip, float vol) {
        if (clip == null) return;
        try {
            FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // Convert linear 0-1 to dB range of the control
            float min = fc.getMinimum();
            float max = 0f; // 0 dB = unity gain
            float dB  = min + (max - min) * vol;
            fc.setValue(Math.max(min, Math.min(max, dB)));
        } catch (IllegalArgumentException ex) {
            // Control not supported on this clip
        }
    }

    public void playMusic() {
        if (musicClip == null || !musicEnabled) return;
        musicClip.setFramePosition(0);
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMusic() {
        if (musicClip != null && musicClip.isRunning())
            musicClip.stop();
    }

    public void playShoot() {
        if (shootClip == null || !soundEnabled) return;
        shootClip.setFramePosition(0);
        shootClip.start();
    }

    public void setMusicEnabled(boolean b) {
        musicEnabled = b;
        if (b) playMusic(); else stopMusic();
    }

    public void setSoundEnabled(boolean b) { soundEnabled = b; }

    public boolean isMusicEnabled() { return musicEnabled; }
    public boolean isSoundEnabled() { return soundEnabled; }

    public float getMasterVolume() { return masterVolume; }

    public void setMasterVolume(float vol) {
        masterVolume = Math.max(0f, Math.min(1f, vol));
        setVolume(musicClip,  masterVolume);
        setVolume(shootClip,  masterVolume);
    }

    public void dispose() {
        if (musicClip  != null) musicClip.close();
        if (shootClip  != null) shootClip.close();
    }
}
