package manager;

import akka.actor.typed.ActorRef;

/**
 * Created on 15.05.2023
 *
 * @author Mykola Horkov
 * <br> mykola.horkov@gmail.com
 */
public class RacerUpdateCommand implements Command {
    private static final long serialVersionUID = 1L;
    private ActorRef<worker.Command> racer;
    private int position;
    private boolean isFinished;
    private double resultTime;

    public RacerUpdateCommand(ActorRef<worker.Command> racer, int position, boolean isFinished, double resultTime) {
        this.racer = racer;
        this.position = position;
        this.isFinished = isFinished;
        this.resultTime = resultTime;
    }

    public ActorRef<worker.Command> getRacer() {
        return racer;
    }

    public int getPosition() {
        return position;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public double getResultTime() {
        return resultTime;
    }
}
