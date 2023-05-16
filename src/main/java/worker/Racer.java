package worker;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import manager.RacerUpdateCommand;

import java.util.Random;

public class Racer extends AbstractBehavior<Command> {

    private Racer(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(Racer::new);
    }

    private final double defaultAverageSpeed = 48.2;
    private int averageSpeedAdjustmentFactor;
    private Random random;


    private double getMaxSpeed() {
        return defaultAverageSpeed * (1 + ((double) averageSpeedAdjustmentFactor / 100));
    }

    private double getDistanceMovedPerSecond(double currentSpeed) {
        return currentSpeed * 1000 / 3600;
    }

    private double determineNextSpeed(int raceLength, int currentPosition, double currentSpeed) {
        if (currentPosition < (raceLength / 4)) {
            currentSpeed = currentSpeed + (((getMaxSpeed() - currentSpeed) / 10) * random.nextDouble());
        } else {
            currentSpeed = currentSpeed * (0.5 + random.nextDouble());
        }

        if (currentSpeed > getMaxSpeed())
            currentSpeed = getMaxSpeed();

        if (currentSpeed < 5)
            currentSpeed = 5;

        if (currentPosition > (raceLength / 2) && currentSpeed < getMaxSpeed() / 2) {
            currentSpeed = getMaxSpeed() / 2;
        }
        return currentSpeed;
    }

    @Override
    public Receive<Command> createReceive() {
        return beforeRace();
    }

    public Receive<Command> beforeRace() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class, message -> {
                    this.random = new Random();
                    this.averageSpeedAdjustmentFactor = random.nextInt(30) - 10;
                    return race(message.getRaceLength(), 0, false, 0, 0);
                })
                .onMessage(PositionCommand.class, message -> {
                    message.getController().tell(new RacerUpdateCommand(getContext().getSelf(), 0, false, 0));
                    return Behaviors.same();
                })
                .build();
    }

    public Receive<Command> race(int raceLength, int currentPosition, boolean isFinished, double currentTime, double currentSpeed) {
        return newReceiveBuilder().onMessage(PositionCommand.class, message -> {
            double speed = determineNextSpeed(raceLength, currentPosition, currentSpeed);
            int newPosition = currentPosition;
            boolean finished = isFinished;
            double time = currentTime;
            newPosition += getDistanceMovedPerSecond(speed);
            if (newPosition >= raceLength) {
                newPosition = raceLength;
                if (!finished)
                    time++;

                finished = true;
            } else {
                time++;
            }
            message.getController().tell(new RacerUpdateCommand(getContext().getSelf(), newPosition, finished, time));
            if (finished) {
                return finished(raceLength, time);
            } else {
                return race(raceLength, newPosition, false, time, speed);
            }
        }).build();
    }

    public Receive<Command> finished(int raceLength, double currentTime) {
        return newReceiveBuilder().onMessage(PositionCommand.class, message -> {
            message.getController().tell(new RacerUpdateCommand(getContext().getSelf(), raceLength, true, currentTime));
            return Behaviors.same();
        }).build();
    }
}
