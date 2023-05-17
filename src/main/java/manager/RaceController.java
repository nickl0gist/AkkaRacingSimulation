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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RaceController extends AbstractBehavior<Command> {

    private RaceController(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(RaceController::new);
    }

    private Map<ActorRef<worker.Command>, RacerData> racerDataMap;
    private long start;
    private static final int RACE_LENGTH = 100;
    private Object TIMER_KEY;

    private void displayRace() {
        int displayLength = 160;
        for (int i = 0; i < 50; ++i) System.out.println();
        System.out.println("Race has been running for " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
        System.out.println("    " + new String(new char[displayLength]).replace('\0', '='));
        racerDataMap.values().stream()
                .sorted(Comparator.comparing(RacerData::getIndex))
                .forEach(v -> {
                    if (v.getCurrentPosition() == RACE_LENGTH) {
                        System.out.println(v.getIndex() + " : " + new String(new char[v.getCurrentPosition() * displayLength / 100]).replace('\0', '*') + " Time: " + getaTime(v.getFinishingTimes()) + "sec.");
                    } else {
                        System.out.println(v.getIndex() + " : " + new String(new char[v.getCurrentPosition() * displayLength / 100]).replace('\0', '*') + ">");
                    }
                });
    }


    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class, message -> {
                    start = System.currentTimeMillis();
                    racerDataMap = new HashMap<>();

                    for (int i = 0; i < 10; i++) {
                        ActorRef<worker.Command> racer = getContext().spawn(Racer.create(), "racer" + i);
                        racerDataMap.put(racer, new RacerData(i, 0));
                        racer.tell(new worker.StartCommand(RACE_LENGTH));
                    }
                    return Behaviors.withTimers(timer -> {
                        timer.startTimerAtFixedRate(TIMER_KEY, new GetPositionsCommand(), Duration.ofSeconds(1));
                        return Behaviors.same();
                    });
                })
                .onMessage(GetPositionsCommand.class, message -> {
                    for (ActorRef<worker.Command> racer : racerDataMap.keySet()) {
                        racer.tell(new PositionCommand(getContext().getSelf()));
                        displayRace();
                    }
                    return Behaviors.same();
                })
                .onMessage(RacerUpdateCommand.class, message -> {
                    RacerData racerData = this.racerDataMap.get(message.getRacer());

                    racerData.setCurrentPosition(message.getPosition());
                    racerData.setFinishingTimes(message.getCurrentTime());
                    return Behaviors.same();
                })
                .onMessage(RacerFinishedCommand.class, message -> {
                    racerDataMap.get(message.getRacer()).setFinishingTimes(message.getResultTime());
                    if (racerDataMap.entrySet().stream().allMatch(e -> e.getValue().getCurrentPosition() == RACE_LENGTH)) {
                        return raceCompleteMessageHandler();
                    }
                    return Behaviors.same();
                }).build();
    }

    public Receive<Command> raceCompleteMessageHandler() {
        return newReceiveBuilder()
                .onMessage(GetPositionsCommand.class, message -> {
                    displayResults();
                    return Behaviors.withTimers(timers -> {
                        timers.cancelAll();
                        return Behaviors.stopped();
                    });
                })
                .build();
    }

    public void displayResults() {
        System.out.println("Results:");
        racerDataMap.values().stream()
                .sorted(Comparator.comparing(RacerData::getFinishingTimes))
                .forEach(v -> System.out.println("Racer#" + v.getIndex() + " finished in " + getaTime(v.getFinishingTimes()) + "sec"));
    }

    private double getaTime(Long time) {
        return ((double) time - start) / 1000;
    }

}
