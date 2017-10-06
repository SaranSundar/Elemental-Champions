import javax.sound.sampled.*;

public class Sound {

    private Clip clip;

    // Change file name to match yours, of course
    public static Sound sound1 = new Sound("testSong.wav");
    public static Sound sound2 = new Sound("testSong2.wav");

    public Sound(String fileName) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(Sound.class.getResource(fileName));
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            if (clip != null) {
                new Thread() {
                    public void run() {
                        synchronized (clip) {
                            clip.stop();
                            clip.setFramePosition(0);
                            clip.start();
                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mute(boolean shouldMute) {
        BooleanControl muteControl = (BooleanControl) clip
                .getControl(BooleanControl.Type.MUTE);
        muteControl.setValue(shouldMute);
    }

    public void setVolume(double gain) {

        FloatControl gainControl = (FloatControl) clip
                .getControl(FloatControl.Type.MASTER_GAIN);
        //gain = .5D; // number between 0 and 1 (loudest)
        float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    }

    public void stop() {
        if (clip == null) return;
        clip.stop();
    }

    public void loop() {
        try {
            if (clip != null) {
                new Thread() {
                    public void run() {
                        synchronized (clip) {
                            clip.stop();
                            clip.setFramePosition(0);
                            clip.loop(Clip.LOOP_CONTINUOUSLY);
                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isActive() {
        return clip.isActive();
    }
}