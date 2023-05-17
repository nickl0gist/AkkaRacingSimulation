package manager;

/**
 * Created on 15.05.2023
 *
 * @author Mykola Horkov
 * <br> mykola.horkov@gmail.com
 */
class RacerData {
    private Integer currentPosition;
    private Long finishingTimes;
    private int index;

    public Long getFinishingTimes() {
        return finishingTimes;
    }

    public void setFinishingTimes(Long finishingTimes){
        this.finishingTimes = finishingTimes;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getIndex() {
        return index;
    }

    public RacerData(int index, Integer currentPosition) {
        this.index = index;
        this.currentPosition = currentPosition;
    }
}
