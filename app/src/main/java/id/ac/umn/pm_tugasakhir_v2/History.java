package id.ac.umn.pm_tugasakhir_v2;

public class History {
    private String historyId;
    private String historyImage;
    private String historyName;
    private String historyDeskripsi1;
    private String historyDeskripsi2;

    public History() {}

    public History(String historyId, String historyImage, String historyName, String historyDeskripsi1, String historyDeskripsi2) {
        this.historyId = historyId;
        this.historyImage = historyImage;
        this.historyName = historyName;
        this.historyDeskripsi1 = historyDeskripsi1;
        this.historyDeskripsi2 = historyDeskripsi2;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getHistoryImage() {
        return historyImage;
    }

    public void setHistoryImage(String historyImage) {
        this.historyImage = historyImage;
    }

    public String getHistoryName() {
        return historyName;
    }

    public void setHistoryName(String historyName) {
        this.historyName = historyName;
    }

    public String getHistoryDeskripsi1() {
        return historyDeskripsi1;
    }

    public void setHistoryDeskripsi1(String historyDeskripsi1) {
        this.historyDeskripsi1 = historyDeskripsi1;
    }

    public String getHistoryDeskripsi2() {
        return historyDeskripsi2;
    }

    public void setHistoryDeskripsi2(String historyDeskripsi2) {
        this.historyDeskripsi2 = historyDeskripsi2;
    }
}
