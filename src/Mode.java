public class Mode {

    Mode(int lives, int speed, String modeName) {
        super();
    }

    public void modeChoose(int modeChoice) {

        switch(modeChoice){
            case 1:
                Paddle.setPaddleSpeed(10);

        }
    }
}
