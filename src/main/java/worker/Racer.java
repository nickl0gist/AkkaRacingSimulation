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

    private double currentSpeed = 0;
    private double currentPosition = 0;
    private int raceLength;
    private boolean isFinished;
    private double currentTime;

    private double getMaxSpeed() {
        return defaultAverageSpeed * (1+((double)averageSpeedAdjustmentFactor / 100));
    }

    private double getDistanceMovedPerSecond() {
        return currentSpeed * 1000 / 3600;
    }

    private void determineNextSpeed() {
        if (currentPosition < (raceLength / 4)) {
            currentSpeed = currentSpeed  + (((getMaxSpeed() - currentSpeed) / 10) * random.nextDouble());
        }
        else {
            currentSpeed = currentSpeed * (0.5 + random.nextDouble());
        }

        if (currentSpeed > getMaxSpeed())
            currentSpeed = getMaxSpeed();

        if (currentSpeed < 5)
            currentSpeed = 5;

        if (currentPosition > (raceLength / 2) && currentSpeed < getMaxSpeed() / 2) {
            currentSpeed = getMaxSpeed() / 2;
        }
    }

    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class, message -> {
                    this.raceLength = message.getRaceLength();
                    this.random = new Random();
                    this.averageSpeedAdjustmentFactor = random.nextInt(30) - 10;
                    return this;
                })
                .onMessage(PositionCommand.class, message -> {
                    determineNextSpeed();
                    currentPosition += getDistanceMovedPerSecond();
                    if (currentPosition >= raceLength){
                        currentPosition = raceLength;
                        if(!isFinished)
                            currentTime += 1;

                        isFinished = true;
                    } else {
                        currentTime += 1;
                    }
                    message.getController().tell(new RacerUpdateCommand(getContext().getSelf(), (int)currentPosition, isFinished, currentTime));
                    return this;
                })
                .build();
    }


}
