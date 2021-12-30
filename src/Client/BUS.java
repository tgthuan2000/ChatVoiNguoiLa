package Client;

import Shares.Key;
import Shares.ServerConfig;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BUS {

    static Socket socket;
    public static BufferedWriter out;
    public static BufferedReader in;
    public static String user = null;
    public static String user2 = null;

    public static void connect() throws IOException {
        if (socket == null) {
            socket = new Socket(ServerConfig.SERVER, ServerConfig.PORT);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Executors.newSingleThreadExecutor().execute(new WorkerClient(socket));
            System.out.println("Client connected");
        }
    }

    public static boolean continute() {
        try {
            while (true) {
                TimeUnit.MILLISECONDS.sleep(500);
                if (WorkerClient.isContinue) {
                    break;
                }
                System.out.println("waitting...");
            }
            WorkerClient.isContinue = false;
        } catch (InterruptedException ex) {
        }
        return true;
    }

    public static void writeLine(String str) throws IOException {
        out.write(str.trim() + "\n");
    }

    public static String readLine() throws IOException {
        return in.readLine();
    }

    public static void flush() throws IOException {
        out.flush();
    }

    public static int login(String usr) {
        try {
            connect();
            writeLine(Key.DANGNHAP);
            writeLine(usr);
            flush();
            return Status.OK;
        } catch (IOException ex) {
            return Status.LOI_KETNOI_SERVER;
        }
    }

    public static int playChat() {
        try {
            writeLine(Key.PLAY_CHAT);
            flush();
            System.out.println("Chờ phòng...");
            return Status.OK;
        } catch (IOException ex) {
            return Status.LOI_KETNOI_SERVER;
        }
    }

    public static int cancleChat() {
        try {
            writeLine(Key.CANCLE_CHAT);
            flush();
            System.out.println("Huỷ chờ");
            return Status.OK;
        } catch (IOException ex) {
            return Status.LOI_KETNOI_SERVER;
        }
    }
}
