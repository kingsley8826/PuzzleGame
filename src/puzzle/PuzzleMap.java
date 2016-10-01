/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzle;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author TuanFPT
 */
public class PuzzleMap {

    private JPanel pnLayout;
    private JLabel lbCount;
    private JLabel lbTime;
    private ArrayList<JButton> arrBtn;
    private int numMove = 0;
    private Timer t;
    private int second;
    private int minues;

    public PuzzleMap(JPanel pnLayout, JLabel lbCount, JLabel lbTime) {
        this.pnLayout = pnLayout;
        this.lbCount = lbCount;
        this.lbTime = lbTime;
    }

    public void countTime() {
        if (t != null) {
            t.stop();
        }
        minues = 0;
        second = 0;
        t = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                second++;
                if (second == 60) {
                    second = 0;
                    minues++;
                }
                DecimalFormat df = new DecimalFormat("00");
                lbTime.setText(df.format(minues) + ":" + df.format(second));
            }
        });
        t.start();
    }

    // Thuật toán check xem PuzzleGame có thắng được hay không
    // Với size lẻ,có thể thắng khi N chẵn
    // Với size chẵn, có thể thắng với N chẵn khi khoảng trắng ở dòng chãn, với N lẻ khi khoảng trắng ở dòng lẻ   
    private boolean checkRandom(int size, int spaceRow) { // Sometimes you can not win because random is wrong
        int N = 0;
        for (int i = 0; i < arrBtn.size() - 1; i++) {
            for (int j = i + 1; j < arrBtn.size(); j++) {
                if (!arrBtn.get(i).getName().equals("0") && !arrBtn.get(j).getName().equals("0")) {
                    if (Integer.parseInt(arrBtn.get(i).getName()) > Integer.parseInt(arrBtn.get(j).getName())) {
                        N++;
                    }
                }
            }
        }
        if (size % 2 != 0 && N % 2 == 0) {
            return true;
        }
        if (size % 2 == 0) {
            if (spaceRow % 2 == 0 && N % 2 == 0) {
                return true;
            } else if (spaceRow % 2 != 0 && N % 2 != 0) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMove(JButton btn, int size) {
        if (btn.getName().equals("0")) {
            return false;
        }
        int startCol = 0;
        int startRow = 0;
        int desCol = 0;
        int desRow = 0;
        for (int i = 0; i < arrBtn.size(); i++) { // tọa độ điểm ấn và điểm trắng
            if (arrBtn.get(i).getName().equals(btn.getName())) {
                startCol = i % size;
                startRow = i / size;
            }
            if (arrBtn.get(i).getName().equals("0")) {
                desCol = i % size;
                desRow = i / size;
            }
        }
        if (startCol == desCol) {
            if (startRow == (desRow - 1) || startRow == (desRow + 1)) {
                return true; // move up or down
            }
        }
        if (startRow == desRow) {
            if (startCol == (desCol - 1) || startCol == (desCol + 1)) {
                return true; // move left or right
            }
        }
        return false;
    }

    private void moveBtn(JButton btn) { // move Button
        Icon icon = null;
        for (int i = 0; i < arrBtn.size(); i++) {
            if (arrBtn.get(i).getName().equals("0")) {
                icon = arrBtn.get(i).getIcon();
                arrBtn.get(i).setName(btn.getName());               
                arrBtn.get(i).setIcon(btn.getIcon());
                break;
            }
        }
        btn.setName("0");
        btn.setIcon(icon);
        numMove++;
        lbCount.setText(String.valueOf(numMove));
    }

    private ArrayList randomMatrix(int num) { // return a random array
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            data.add(String.valueOf(i));
        }
        Collections.shuffle(data); // merge list
        return data;
    }

    public void initButon(int size) { // khởi tạo button ban đầu
        arrBtn = new ArrayList<>();
        ArrayList<String> data = randomMatrix(size * size);
        
        pnLayout.revalidate(); // this funtion is very very important if you want to draw
        pnLayout.removeAll();
        pnLayout.setLayout(new GridLayout(size, size, 0, 0));
        for (int i = 0; i < data.size(); i++) {
            JButton btn = new JButton();     
            btn.setName(data.get(i));              
            Image img = null;
            String imageName = "/images/" + size + "/" + size + "." + data.get(i) + ".png";
            try {
                img = ImageIO.read(getClass().getResource(imageName));
            } catch (IOException ex) {
                Logger.getLogger(PuzzleMap.class.getName()).log(Level.SEVERE, null, ex);
            }
            btn.setIcon(new ImageIcon(img));
//            btn.setContentAreaFilled (false); // làm trong suốt button rỗng          
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkMove(btn, size)) {
                        moveBtn(btn);
                        if (checkWin()) {
                            t.stop();
                            isWon();
                        }
                    }
                }
            });
            pnLayout.add(btn);
            arrBtn.add(btn);
        }
        // checkRandom
        int spaceRow = 0;
        for (int i = 0; i < arrBtn.size(); i++) {
            if (arrBtn.get(i).getText().equals("")) {
                spaceRow = i / size + 1;
                break;
            }
        }
        if (!checkRandom(size, spaceRow)) {
            initButon(size);
        }
    }

    private boolean checkWin() {
        ArrayList<String> btnName = new ArrayList<>();
        for (int i = 0; i < arrBtn.size()-1; i++) { // ko lấy button cuối vì đó là khoảng trắng
            btnName.add(arrBtn.get(i).getName());
        }
        for (int i = 0; i < btnName.size(); i++) {
            for (int j = i; j < btnName.size(); j++) {
                if(btnName.get(j).equals("0")){
                    return false;
                }
                if (Integer.parseInt(btnName.get(j)) < Integer.parseInt(btnName.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void isWon() {
        JOptionPane.showMessageDialog(null, "You Won!");
    }
}
