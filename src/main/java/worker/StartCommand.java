package worker;

/**
 * Created on 15.05.2023
 *
 * @author Mykola Horkov
 * <br> mykola.horkov@gmail.com
 */
public class StartCommand implements Command {
    private static final long serialVersionUID = 1L;
    private int raceLength;

    public StartCommand(int raceLength) {
        this.raceLength = raceLength;
    }

    public int getRaceLength() {
        return raceLength;
    }
}
