package Client;

import Client.GUI.Chat;
import Client.GUI.Form;
import Client.GUI.Main;
import Shares.Key;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class WorkerClient implements Runnable {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public static int status;
    public static boolean isContinue;

    public WorkerClient(Socket s) throws IOException {
        this.socket = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        isContinue = false;
    }

    private void writeLine(String str) throws IOException {
        out.write(str.trim() + "\n");
    }

    private String readLine() throws IOException {
        return in.readLine();
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    switch (in.readLine()) {
                        case Key.FAILD:
                            faild();
                            break;
                        case Key.OK:
                            ok();
                            break;

                        //
                        // IN MAIN
                        //
                        case Key.HAVE_CHAT:
                            haveChat();
                            break;
                        case Key.CANCLE_CHAT:
                            cancleChat();
                            break;
                        case Key.NO_CONTINUE_CHAT:
                            noContinueChat();
                            break;

                        //
                        // IN CHAT
                        //
                        case Key.LOAD_CHAT:
                            loadChat();
                            break;
                        case Key.INFO_USER_2:
                            infoUser2();
                            break;
                        case Key.PREPARE_OK:
                            prepareOk();
                            break;
                        case Key.PREPARE_FAILD:
                            prepareFaild();
                            break;
                        case Key.USER_OUTCHAT:
                            userOutChat();
                            break;
                        case Key.MESSAGE:
                            message();
                            break;
                    }
                } catch (IOException ex) {
                    break;
                } catch (Exception ex) {
                    Logger.getLogger(WorkerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(WorkerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void faild() {
        status = Status.FAILD;
        isContinue = true;
    }

    private void ok() {
        status = Status.OK;
        isContinue = true;
    }

    private void haveChat() {
        try {
            String roomId = readLine();
            System.out.println("Có người chat");
            System.out.println("Room id: " + roomId);

            TimeUnit.MILLISECONDS.sleep(2000);
            Main.flag = true;
            writeLine(Key.CHECK_ACCEPT_CHAT);
            if (JOptionPane.showConfirmDialog(null, "Chấp nhận vào phòng chat ?") == JOptionPane.YES_OPTION) {
                writeLine(Key.OK);
                System.out.println("Chấp nhận vào phòng");

                // block ng dùng
                Main.btnPlay.setEnabled(false);

            } else {
                writeLine(Key.FAILD);
                System.out.println("Từ chối vào phòng");
            }
            writeLine(roomId); // room id
            out.flush();
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
            Logger.getLogger(WorkerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cancleChat() throws IOException {
        Main.btnPlay.setText("Bắt đầu");
        Main.thGian = 0;
        Main.lbTime.setText("");
    }

    private void noContinueChat() {
        Main.btnPlay.setEnabled(true);
        Main.flag = false;
        Main.waitting();
        JOptionPane.showMessageDialog(null, "Phòng chat bị huỷ do đối phương không chấp nhận!!!");
    }

    private void loadChat() throws IOException {
        writeLine(Key.LOAD_CHAT);
        out.flush();
        Form.hideMain();
    }

    private void infoUser2() throws IOException {
        BUS.user2 = readLine();
    }

    private void prepareOk() throws Exception {
        Form.newChat();
    }

    private void prepareFaild() throws Exception {
        JOptionPane.showMessageDialog(null, "Phòng chat đã bị huỷ do gặp sự cố!!!");
        Form.newMain();
    }

    private void userOutChat() throws IOException {
        writeLine(Key.CLEAR_OLDCHAT);
        out.flush();
        JOptionPane.showMessageDialog(null, "Đối phương đã thoát phòng chat!");
        Form.hideChat();
        Form.newMain();
    }

    private void message() throws IOException {
        Chat.textArea.append(BUS.user2 + ": " + readLine() + "\n");
    }
}
