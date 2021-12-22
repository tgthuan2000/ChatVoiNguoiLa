package Server;

import Shares.Key;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker implements Runnable {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private String user = null;
    public String user2 = null;
    private String roomId = null;
    public Worker workerUser2;

    public Worker(Socket s) throws IOException, Exception {
        this.socket = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }

    private void writeLine(String str) throws IOException {
        out.write(str.trim() + "\n");
    }

    private String readLine() throws IOException {
        return in.readLine();
    }

    @Override
    public void run() {
        System.out.println("Client " + socket.toString() + " accepted");
        try {
            while (true) {
                try {
                    switch (in.readLine()) {
                        case Key.DANGNHAP:
                            dangNhap();
                            break;
                        case Key.PLAY_CHAT:
                            playChat();
                            break;
                        case Key.CANCLE_CHAT:
                            cancleChat();
                            break;
                        case Key.CHECK_ACCEPT_CHAT:
                            checkAcceptChat();
                            break;
                        case Key.LOAD_CHAT:
                            prepare();
                            break;
                        case Key.REFRESH_FOR_OUTCHAT:
                            refresh();
                            break;
                        case Key.CLEAR_OLDCHAT:
                            clearOldChat();
                            break;
                        case Key.MESSAGE:
                            message();
                            break;
                    }
                } catch (IOException ex) {
                    break;
                }
            }
            cleanup();
            System.out.println("user " + user + " disconected");
            System.out.println("User online: " + ServerMain.users.size());
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cleanup() throws IOException {
        cancleGameWhenUserDisconected();
        ServerMain.users_waitting.remove(user);
        ServerMain.users.remove(user);
        ServerMain.workers.remove(this);
    }

    private void cancleGameWhenUserDisconected() throws IOException {
        for (Room r : ServerMain.waittingRooms) {
            if (r.getUser1().equals(user)) {
                r.setUser1Accept(r.DENY);
                System.out.println("user " + user + " disconnected game");
                break;
            } else if (r.getUser2().equals(user)) {
                r.setUser2Accept(r.DENY);
                System.out.println("user " + user + " disconnected game");
                break;
            }
        }
    }

    //
    // LOGIN
    //
    private void dangNhap() {
        try {
            String username = readLine();
            boolean flag = true;
            // kiểm tra user có online chưa
            for (String usr : ServerMain.users) {
                if (usr.equals(username)) {
                    flag = false;
                    break;
                }
            }

            if (flag && ServerMain.users.add(username)) { // user online
                user = username;
                System.out.println(user + " đăng nhập thành công");
                writeLine(Key.OK);
            } else {
                System.out.println("Tài khoản đã đăng nhập");
                writeLine(Key.FAILD);
            }
            out.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    //
    // EVENT CHAT
    //
    private void playChat() {
        try {
            if (ServerMain.users_waitting.add(user)) {
                System.out.println("Cho người dùng " + user + " vào phòng chờ");
                writeLine(Key.OK);
                out.flush();
                ghepcap();
            } else {
                writeLine(Key.FAILD);
                out.flush();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void ghepcap() throws IOException {
        int size = ServerMain.users_waitting.size();

        if (size == 2) {
            sendAccept(0, 1);
        } else if (size > 2) {
            Random random = new Random();
            int usr1;
            int usr2;
            do {
                usr1 = random.nextInt(size);
                usr2 = random.nextInt(size);
            } while (usr1 == usr2);

            sendAccept(usr1, usr2);
        }
    }

    private void sendAccept(int user1, int user2) throws IOException {
        String usr1 = ServerMain.users_waitting.get(user1);
        String usr2 = ServerMain.users_waitting.get(user2);

        Room waittingRoom = new Room();
        String id = "room-" + (new Random().nextInt(89999999) + 10000000);
        waittingRoom.setRoomID(id);
        System.out.println("Room id: " + id);
        for (Worker worker : ServerMain.workers) {
            String usr = worker.user;
            if (usr.equals(usr1)) {
                System.out.println("Gửi accept user " + usr1);
                sendAccept(worker, id);
                waittingRoom.setUser1(worker.user);
            }
            if (usr.equals(usr2)) {
                System.out.println("Gửi accept user " + usr2);
                sendAccept(worker, id);
                waittingRoom.setUser2(worker.user);
            }
        }
        ServerMain.waittingRooms.add(waittingRoom);
    }

    private void sendAccept(Worker worker, String roomId) throws IOException {
        worker.writeLine(Key.HAVE_CHAT);
        worker.writeLine(roomId);
        worker.out.flush();
        ServerMain.users_waitting.remove(worker.user);
    }

    private void cancleChat() {
        try {
            if (ServerMain.users_waitting.remove(user)) {
                System.out.println("Người dùng " + user + " đã thoát phòng chờ");
                writeLine(Key.OK);
            } else {
                writeLine(Key.FAILD);
            }
            out.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void checkAcceptChat() throws IOException {
        switch (readLine()) {
            case Key.OK:
                accept();
                break;
            case Key.FAILD:
                deny();
                break;
        }
    }

    private void accept() throws IOException {
        roomId = readLine();
        // kiểm tra user nào click accept bằng roomid
        for (Room r : ServerMain.waittingRooms) {
            if (r.getRoomID().equals(roomId)) {
                int currentUser = 0;
                if (r.getUser1().equals(user)) {
                    currentUser = 1;
                    r.setUser1Accept(r.ACCEPT);
                    user2 = r.getUser2();
                    System.out.println("user " + user + " accept");
                } else {
                    r.setUser2Accept(r.ACCEPT);
                    System.out.println("user " + user + " accept");
                    user2 = r.getUser1();
                }

                // khi user này accept mà có 1 user deny trước đó
                if (r.getUser1Accept() == r.DENY || r.getUser2Accept() == r.DENY) {
                    if (ServerMain.waittingRooms.remove(r) && ServerMain.users_waitting.add(user)) {
                        System.out.println("Huỷ room: " + r.getRoomID());
                        System.out.println("Cho người dùng " + user + " vào lại hàng chờ");
                        writeLine(Key.NO_CONTINUE_CHAT);
                        out.flush();
                        roomId = null;
                        user2 = null;
                        ghepcap();
                    }
                }

                // cuối
                if (r.getUser1Accept() == r.ACCEPT && r.getUser2Accept() == r.ACCEPT) {
                    System.out.println("2 user accept");
                    if (currentUser == 1) {
                        for (Worker worker : ServerMain.workers) {
                            if (worker.user != null && worker.user.equals(r.getUser2())) {
                                loadChat(worker);
                                break;
                            }
                        }
                    } else {
                        for (Worker worker : ServerMain.workers) {
                            if (worker.user.equals(r.getUser1())) {
                                loadChat(worker);
                                break;
                            }
                        }
                    }
                    ServerMain.waittingRooms.remove(r);
                }
                break;
            }
        }
    }

    private void loadChat(Worker worker) throws IOException {
        writeLine(Key.LOAD_CHAT);
        out.flush();
        worker.writeLine(Key.LOAD_CHAT);
        worker.out.flush();
        ServerMain.chatWorkers.add(this);
        ServerMain.chatWorkers.add(worker);
    }

    private void deny() throws IOException {
        roomId = readLine();
        // kiểm tra user nào click deny bằng roomid
        for (Room r : ServerMain.waittingRooms) {
            if (r.getRoomID().equals(roomId)) {
                if (r.getUser1().equals(user)) {
                    r.setUser1Accept(r.DENY);
                    System.out.println("user " + user + " deny");

                    // kiểm tra user trước đó nhấn accept
                    if (r.getUser2Accept() == r.ACCEPT) {
                        for (Worker worker : ServerMain.workers) {
                            if (worker.user.equals(r.getUser2())) {
                                System.out.println("Huỷ room: " + r.getRoomID());
                                ServerMain.waittingRooms.remove(r);
                                sendCancleChat(worker);
                                ghepcap();
                                break;
                            }
                        }
                    }
                } else {
                    r.setUser2Accept(r.DENY);
                    System.out.println("user " + user + " deny");

                    // kiểm tra user trước đó nhấn accept
                    if (r.getUser1Accept() == r.ACCEPT) {
                        for (Worker worker : ServerMain.workers) {
                            if (worker.user.equals(r.getUser1())) {
                                System.out.println("Huỷ room: " + r.getRoomID());
                                ServerMain.waittingRooms.remove(r);
                                sendCancleChat(worker);
                                ghepcap();
                                break;
                            }
                        }
                    }
                }

                System.out.println("Người dùng " + user + " đã thoát phòng chờ");
                roomId = null;
                writeLine(Key.CANCLE_CHAT);
                out.flush();

                if (r.getUser1Accept() == r.DENY && r.getUser2Accept() == r.DENY) {
                    ServerMain.waittingRooms.remove(r);
                    System.out.println("Huỷ room: " + r.getRoomID());
                }
                break;
            }
        }
    }

    private void sendCancleChat(Worker worker) throws IOException {
        ServerMain.users_waitting.add(worker.user);
        System.out.println("Add user " + worker.user + " vào hàng chờ");
        worker.writeLine(Key.NO_CONTINUE_CHAT);
        worker.out.flush();
        worker.roomId = null;
    }

    //
    // IN CHAT
    //
    private void prepare() throws IOException {
        workerUser2 = getUser2();
        if (workerUser2 != null) {
            guiInfoUser2();
            writeLine(Key.PREPARE_OK);
            System.out.println("Khởi tạo user " + user + " hoàn tất!");
        } else {
            writeLine(Key.PREPARE_FAILD);
            System.out.println("Khởi tạo user " + user + " thất bại!");
        }
        out.flush();
    }

    private Worker getUser2() {
        for (Worker wk : ServerMain.chatWorkers) {
            if (wk.roomId.equals(roomId) && wk.user.equals(user2)) {
                return wk;
            }
        }
        return null;
    }

    private void guiInfoUser2() throws IOException {
        writeLine(Key.INFO_USER_2);
        writeLine(user2);
        out.flush();
    }

    private void refresh() throws IOException {
        workerUser2.writeLine(Key.USER_OUTCHAT);
        workerUser2.out.flush();
        clearOldChat();
    }

    private void clearOldChat() {
        workerUser2 = null;
        user2 = null;
        roomId = null;
        ServerMain.chatWorkers.remove(this);
    }

    private void message() throws IOException {
        workerUser2.writeLine(Key.MESSAGE);
        workerUser2.writeLine(readLine());
        workerUser2.out.flush();
    }

}
