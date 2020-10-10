package Presentation;

public class TotalStatistics {

    /**
     * @return the stats
     */
    public static String getStats() {
        if (PresentationController.getInstance().isCompressed()) {
            return decompressionStats();
        }
        else {
            return compressionStats();
        }
    }

    /**
     * Indicates read, written, elapsed time, compression ratio, compression per second from the total statistics files
     * @return the total statistics from compression
     */
    private static String compressionStats() {
        long in = PresentationController.getInstance().getTotalInputSizeStat();
        long out = PresentationController.getInstance().getTotalOutputSizeStat();
        long time = PresentationController.getInstance().getTotalTimeStat();
        if (time < 1) {
            time = 1; // 1ms precision
        }
        String stats = "";
        stats = stats + String.format("Compression Ratio: %s (Bigger is better, lower than 1 is bad)\n", String.format("%.2f",(double)(in)/(double)(out)));
        stats = stats + String.format("Space Savings: %s (Bigger is better)\n", String.format("%.2f", 1-((double)(out)/(double)(in))));
        stats = stats + String.format("Read: %s\n", bytesToHumanLegible(in));
        stats = stats + String.format("Written: %s\n", bytesToHumanLegible(out));
        stats = stats + String.format("Elapsed Time: %s\n", milisToHumanLegible(time));
        stats = stats + String.format("Compression per Second: %s\n", bytesToHumanLegible((long)((double)(in)/((double)(time/1000.0)))));
        return stats;
    }

    /**
     * Indicates read, written, elapsed time, decompression ratio, decompression per second from the total statistics files
     * @return the total statistics from decompression
     */
    private static String decompressionStats() {
        long in = PresentationController.getInstance().getTotalInputSizeStat();
        long out = PresentationController.getInstance().getTotalOutputSizeStat();
        long time = PresentationController.getInstance().getTotalTimeStat();
        if (time < 1) {
            time = 1; // 1ms precision
        }
        String stats = "";
        stats = stats + String.format("Decompression Ratio: %s (Inverse of compression ratio, Lower is better)\n", String.format("%.2f",(double)(in)/(double)(out)));
        stats = stats + String.format("Read: %s\n", bytesToHumanLegible(in));
        stats = stats + String.format("Written: %s\n", bytesToHumanLegible(out));
        stats = stats + String.format("Elapsed Time: %s\n", milisToHumanLegible(time));
        stats = stats + String.format("Decompression per Second: %s\n", bytesToHumanLegible((long)((double)(out)/((double)(time/1000.0)))));
        return stats;
    }

    /**
     * Byte converter to facilitate vision
     * @param bytes bytes to deal with
     * @return the respective bytes conversion to GB, MB, KB, B  
     */
    public static String bytesToHumanLegible(long bytes) {
        if (bytes >= 1073741824) {
            return String.format("%.2f", bytes/1073741824.0) + " GB";
        }
        else if (bytes >= 1048576)
            return String.format("%.2f", bytes/1048576.0) + " MB";
        else if (bytes >= 1024)
            return String.format("%.2f", bytes/1024.0) + " KB";
        else
            return String.valueOf(bytes) + " Bytes";
    }

    /**
     * Milliseconds conversion to facilitate vision
     * @param ms milliseconds to deal with
     * @return the respective ms conversion to hours, minutes, seconds and milliseconds
     */
    public static String milisToHumanLegible(long ms) {
        if (ms >= 3600000) {
            return String.format("%.2f", ms/3600000.0) + " Hours";
        }
        else if (ms >= 60000)
            return String.format("%.2f", ms/60000.0) + " Minutes";
        else if (ms >= 1000)
            return String.format("%.2f", ms/1000.0) + " Seconds";
        else
            return String.valueOf(ms) + " Milliseconds";
    }
}