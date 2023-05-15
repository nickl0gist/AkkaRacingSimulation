import akka.actor.typed.ActorSystem;
import manager.Command;
import manager.RaceController;
import manager.StartCommand;

public class Main {
    public static void main(String[] args) {
        ActorSystem<Command> raceController = ActorSystem.create(RaceController.create(), "RaceSimulation");
        raceController.tell(new StartCommand());
    }
}
