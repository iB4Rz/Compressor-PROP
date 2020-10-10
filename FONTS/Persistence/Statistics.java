package Persistence;

public class Statistics {
    /**
     * execution time in ms statistic
     */
    private long executionTime = 0;
    /**
     * input size in bytes statistic
     */
    private long inputSize = 0;
    /**
     * output size in bytes statistic
     */
    private long outputSize = 0;

    /**
     * @param executionTime the executionTime to set
     */
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * @param inputSize the inputSize to set
     */
    public void setInputSize(long inputSize) {
        this.inputSize = inputSize;
    }

    /**
     * @param outputSize the outputSize to set
     */
    public void setOutputSize(long outputSize) {
        this.outputSize = outputSize;
    }

    /**
     * @return the executionTime
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * @return the inputSize
     */
    public long getInputSize() {
        return inputSize;
    }

    /**
     * @return the outputSize
     */
    public long getOutputSize() {
        return outputSize;
    }
}