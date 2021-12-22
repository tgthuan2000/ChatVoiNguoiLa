package Client.GUI;

import Client.BUS;
import Client.Status;
import Client.WorkerClient;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main extends javax.swing.JFrame {

    public static boolean flag;
    public static int thGian;

    public Main() {
        try {
            UIManager.setLookAndFeel(new com.jtattoo.plaf.graphite.GraphiteLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
        this.setTitle("Trang chủ");
        this.setLocationRelativeTo(null);
        Form.main = this;
        flag = true;
        thGian = 0;
        lbUserName.setText(BUS.user);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnMain = new javax.swing.JPanel();
        lbUserName = new javax.swing.JLabel();
        lbTime = new javax.swing.JLabel();
        btnPlay = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        pnMain.setBackground(new java.awt.Color(255, 255, 255));

        lbUserName.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lbUserName.setText("UserName");

        lbTime.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbTime.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        btnPlay.setBackground(new java.awt.Color(255, 255, 255));
        btnPlay.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        btnPlay.setText("BẮT ĐẦU");
        btnPlay.setBorder(new javax.swing.border.MatteBorder(null));
        btnPlay.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPlay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnPlayMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnMainLayout = new javax.swing.GroupLayout(pnMain);
        pnMain.setLayout(pnMainLayout);
        pnMainLayout.setHorizontalGroup(
            pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMainLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnMainLayout.createSequentialGroup()
                        .addComponent(lbUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbTime, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25))
        );
        pnMainLayout.setVerticalGroup(
            pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMainLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbUserName, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btnPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(pnMain, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPlayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPlayMouseClicked
        if (btnPlay.isEnabled()) {
            if (flag) {
                playChat();
            } else {
                cancleChat();
            }
        }
    }//GEN-LAST:event_btnPlayMouseClicked

    private void playChat() {
        switch (BUS.playChat()) {
            case Status.OK:
                if (BUS.continute()) {
                    switch (WorkerClient.status) {
                        case Status.OK:
                            flag = false;
                            btnPlay.setText("Huỷ");
                            waitting();
                            break;
                        case Status.FAILD:
                            JOptionPane.showMessageDialog(rootPane, "Gia nhập hàng chờ thất bại");
                            break;
                    }
                }
                break;
            case Status.LOI_KETNOI_SERVER:
                JOptionPane.showMessageDialog(rootPane, "Lỗi kết nối server");
                break;
        }
    }

    public static void waitting() {
        new Timer("Timer").schedule(new TimerTask() {
            @Override
            public void run() {
                if (!flag) {
                    lbTime.setText(setThoiGian(thGian++));
                } else {
                    cancel();
                }
            }
        }, 0, 1000L);
    }

    private static String setThoiGian(int second) {
        String minute = "00" + (second / 60);
        minute = minute.substring(minute.length() - 2, minute.length());

        String sec = "00" + (second % 60);
        sec = sec.substring(sec.length() - 2, sec.length());

        return minute + ":" + sec;
    }

    private void cancleChat() {
        switch (BUS.cancleChat()) {
            case Status.OK:
                if (BUS.continute()) {
                    switch (WorkerClient.status) {
                        case Status.OK:
                            flag = true;
                            btnPlay.setText("Bắt đầu");
                            lbTime.setText("");
                            thGian = 0;
                            break;
                        case Status.FAILD:
                            JOptionPane.showMessageDialog(rootPane, "Lỗi thoát hàng chờ");
                            break;
                    }
                }
                break;
            case Status.LOI_KETNOI_SERVER:
                JOptionPane.showMessageDialog(rootPane, "Lỗi kết nối server");
                break;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JButton btnPlay;
    public static javax.swing.JLabel lbTime;
    private javax.swing.JLabel lbUserName;
    private javax.swing.JPanel pnMain;
    // End of variables declaration//GEN-END:variables
}
