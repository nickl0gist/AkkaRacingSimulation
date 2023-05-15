package manager;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import worker.PositionCommand;
import worker.Racer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class RaceController extends AbstractBehavior<Command> {

    private RaceController(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(RaceController::new);
    }

    private Map<ActorRef<worker.Command> , RacerData> racerData;
    private long start;
    private int raceLength = 100;
    private Object TIMER_KEY;

    private void displayRace() {
        int displayLength = 160;
        for (int i = 0; i < 50; ++i) System.out.println();
        System.out.println("Race has been running for " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
        System.out.println("    " + new String (new char[displayLength]).replace('\0', '='));
        int i = 0;
        for(Map.Entry<ActorRef<worker.Command> , RacerData> entry : racerData.entrySet()){
            if(entry.getValue().getStatus()){
                System.out.println(i++ + " : "  + new String (new char[entry.getValue().getCurrentPosition() * displayLength / 100]).replace('\0', '*') + " Time: " + entry.getValue().getTime() + "sec.");
            } else {
                System.out.println(i++ + " : "  + new String (new char[entry.getValue().getCurrentPosition() * displayLength / 100]).replace('\0', '*') + ">");
            }
        }
    }


    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class, message -> {
                    start = System.currentTimeMillis();
                    racerData = new HashMap<>();

                    for (int i = 0; i < 10; i++) {
                        ActorRef<worker.Command> racer = getContext().spawn(Racer.create(), "racer"+i);
                        racerData.put(racer, new RacerData(0, false, 0.0));
                        racer.tell(new worker.StartCommand(raceLength));
                    }
                    return Behaviors.withTimers(timer -> {
                        timer.startTimerAtFixedRate(TIMER_KEY, new GetPositionsCommand(), Duration.ofSeconds(1));
                        return this;
                    });
                })
                .onMessage(GetPositionsCommand.class, message -> {
                    for (ActorRef<worker.Command> racer : racerData.keySet()) {
                        racer.tell(new PositionCommand(getContext().getSelf()));
                        displayRace();
                    }
                    if(racerData.entrySet().stream().allMatch(e -> e.getValue().getStatus())){
                        return Behaviors.stopped();
                    }
                    return this;
                })
                .onMessage(RacerUpdateCommand.class, message -> {
                    racerData.put(message.getRacer(), new RacerData(message.getPosition(), message.isFinished(), message.getResultTime()));
                    return this;
                })
                .build();
    }


}
