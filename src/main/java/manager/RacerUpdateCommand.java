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
    private long currentTime;

    public RacerUpdateCommand(ActorRef<worker.Command> racer, int position) {
        this.racer = racer;
        this.position = position;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public ActorRef<worker.Command> getRacer() {
        return racer;
    }

    public int getPosition() {
        return position;
    }
}
