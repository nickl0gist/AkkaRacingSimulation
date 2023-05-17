package manager;

import akka.actor.typed.ActorRef;

/**
 * Created on 17.05.2023
 *
 * @author Mykola Horkov
 * <br> mykola.horkov@gmail.com
 */
public class RacerFinishedCommand implements Command{
    private static final long serialVersionUID = 1L;
    private ActorRef<worker.Command> racer;
    private long resultTime;

    public RacerFinishedCommand(ActorRef<worker.Command> racer, long resultTime) {
        this.racer = racer;
        this.resultTime = resultTime;
    }

    public long getResultTime() {
        return resultTime;
    }

    public ActorRef<worker.Command> getRacer() {
        return racer;
    }
}
