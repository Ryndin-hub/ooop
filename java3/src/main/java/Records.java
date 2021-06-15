import java.io.*;

public class Records {
    private final String[] recordsStr = new String[100];
    private final double[] recordsTimes = new double[100];

    public Records() throws IOException {
        BufferedReader recordsFile = new BufferedReader(new FileReader("src/main/resources/records.txt"));
        for (int i = 0; i < 100; i++){
            recordsStr[i] = recordsFile.readLine();
        }
        for (int i = 0; i < 100; i++) {
            String[] words = recordsStr[i].split(" ");
            recordsTimes[i] = Double.parseDouble(words[0].substring(5,8)) + 1000 * Double.parseDouble(words[0].substring(2,4)) + 60000 * Double.parseDouble(words[0].substring(0,1));
        }
        recordsFile.close();
    }

    public void addRecord(double time, String carName) throws IOException {
        for (int i = 0; i < 100; i++){
            if (time < recordsTimes[i]){
                for (int j = 99; j > i; j--){
                    recordsStr[j] = recordsStr[j-1];
                    recordsTimes[j] = recordsTimes[j-1];
                }
                recordsTimes[i] = time;
                int minutes = (int)time/60000;
                recordsStr[i] = minutes + ":";
                int seconds = (int)(time-minutes*60000)/1000;
                if (seconds < 10){
                    recordsStr[i] += "0" + seconds + ":";
                } else {
                    recordsStr[i] += seconds + ":";
                }
                int milliSeconds = (int)(time-minutes*60000-seconds*1000);
                if (milliSeconds < 10){
                    recordsStr[i] += "00" + milliSeconds + " " + carName;
                } else if (milliSeconds < 100){
                    recordsStr[i] += "0" + milliSeconds + " " + carName;
                } else {
                    recordsStr[i] += milliSeconds + " " + carName;
                }
                System.out.println("New record "+(i+1)+" - "+recordsStr[i]);
                BufferedWriter recordsFileWriter = new BufferedWriter(new FileWriter("src/main/resources/records.txt"));
                for (int k = 0; k < 100; k++){
                    recordsFileWriter.write(recordsStr[k] + "\n");
                }
                recordsFileWriter.close();
                return;
            }
        }
        String lapTime;
        int minutes = (int)time/60000;
        lapTime = minutes + ":";
        int seconds = (int)(time-minutes*60000)/1000;
        if (seconds < 10){
            lapTime += "0" + seconds + ":";
        } else {
            lapTime += seconds + ":";
        }
        int milliSeconds = (int)(time-minutes*60000-seconds*1000);
        if (milliSeconds < 10){
            lapTime += "00" + milliSeconds;
        } else if (milliSeconds < 100){
            lapTime += "0" + milliSeconds;
        } else {
            lapTime += milliSeconds + "";
        }
        System.out.println(carName + " " + lapTime);
    }

    public void printRecords(){
        for (int i = 0; i < 10; i++){
            System.out.println(recordsStr[i]);
        }
    }
}
