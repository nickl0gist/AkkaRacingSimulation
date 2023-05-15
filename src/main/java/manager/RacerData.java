package manager;

/**
 * Created on 15.05.2023
 *
 * @author Mykola Horkov
 * <br> mykola.horkov@gmail.com
 */
class RacerData {
    private Integer currentPosition;
    private Boolean status;
    private Double time;


    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public Boolean getStatus() {
        return status;
    }

    public Double getTime() {
        return time;
    }

    public RacerData(Integer currentPosition, Boolean status, Double time) {
        this.currentPosition = currentPosition;
        this.status = status;
        this.time = time;
    }
}
