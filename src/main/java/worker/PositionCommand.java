package worker;

import akka.actor.typed.ActorRef;

/**
 * Created on 15.05.2023
 *
 * @author Mykola Horkov
 * <br> mykola.horkov@gmail.com
 */
public class PositionCommand implements Command {
    private static final long serialVersionUID = 1L;
    private ActorRef<manager.Command> controller;

    public PositionCommand(ActorRef<manager.Command> controller) {
        this.controller = controller;
    }

    public ActorRef<manager.Command> getController() {
        return controller;
    }
}
