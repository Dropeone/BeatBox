package BeatBox;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
public class BeatBox2 {
    JPanel mainPanel;
    Icon emptyIcon = new ImageIcon("out\\img\\icon.png");

    Icon en = new ImageIcon("out\\img\\icon2.png");

    Font names = new Font("out\\Img\\Cyberpunk_RUS_BY_LYAJKA.ttf", Font.TRUETYPE_FONT, 14);

    Color mainColor = new Color(1, 1, 43, 255);

    Color backGray = new Color(207, 247, 254);

    Color purple = new Color(62, 230, 244);

    Color text = new Color(60, 219, 233);
    String str;

    ArrayList<JCheckBox> checkboxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;
    String[] instrumentNames = {"Бас-барабан", "Закрытый хай-хет", "Открытый хай-хет", "Акустический малый барабан", "Краш Тарелка", "Хлопок в ладоши",
            "Высокий Том", "Бонго", "Маракасы", "Свисток", "Низкая Конга",
            "Ковбелл", "Вибрашлеп", "Низко-средний Том", "Хай Агого",
            "Открытый Конга"};
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
    public static void main(String[] args) {
        new BeatBox2().buildGUI();
    }

    public void buildGUI() {


        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Старт");
        start.setForeground(Color.cyan);
        start.setSelected(false);
        start.setFocusPainted(false);
        start.setBorderPainted(false);
        start.setContentAreaFilled(false);
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Стоп");
        stop.setForeground(Color.cyan);
        stop.setSelected(false);
        stop.setFocusPainted(false);
        stop.setBorderPainted(false);
        stop.setContentAreaFilled(false);
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Увеличение темпа");
        upTempo.setForeground(Color.cyan);
        upTempo.setSelected(false);
        upTempo.setFocusPainted(false);
        upTempo.setBorderPainted(false);
        upTempo.setContentAreaFilled(false);
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Уменьшение темпа");
        downTempo.setForeground(Color.cyan);
        downTempo.setSelected(false);
        downTempo.setFocusPainted(false);
        downTempo.setBorderPainted(false);
        downTempo.setContentAreaFilled(false);
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton clear = new JButton("Очистить");
        clear.setForeground(Color.cyan);
        clear.setSelected(false);
        clear.setFocusPainted(false);
        clear.setBorderPainted(false);
        clear.setContentAreaFilled(false);
        clear.addActionListener(new MyClearListener());
        buttonBox.add(clear);

        JButton save = new JButton("Сохранить");
        save.setForeground(Color.cyan);
        save.setSelected(false);
        save.setFocusPainted(false);
        save.setBorderPainted(false);
        save.setContentAreaFilled(false);
        save.addActionListener(new MySendListener());
        buttonBox.add(save);

        JButton open = new JButton("Открыть");
        open.setForeground(Color.cyan);
        open.setSelected(false);
        open.setFocusPainted(false);
        open.setBorderPainted(false);
        open.setContentAreaFilled(false);
        open.addActionListener(new MyReadInListener());
        buttonBox.add(open);


        Box nameBox = new Box(BoxLayout.Y_AXIS);
        Label temp;
        for (int i = 0; i < 16; i++) {
            temp = new Label (instrumentNames[i]);
            temp.setForeground(text);
            temp.setFont(names);
            nameBox.add(temp);
            nameBox.setBackground(mainColor);
        }

        JTextField field = new JTextField("NAME.seq");
        JTextField fd = new JTextField("NAME");
        field.setBackground((Color) mainColor);

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);
        theFrame.getContentPane().add(background);
        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER,mainPanel);

        mainPanel.setBackground((Color) mainColor);
        background.setBackground((Color) mainColor);
        buttonBox.setBackground((Color) mainColor);
        nameBox.setBackground((Color) mainColor);

        for(int i = 0; i < 256; i++){
            JCheckBox c = new JCheckBox("", false);
            c.setIcon(emptyIcon);
            c.setSelected(false);
            c.setBackground(backGray);
            checkboxList.add(c);
            mainPanel.add(c);
            c.addActionListener(new MyCheckBoxListener());
        }
        setUpMidi();
        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    }



    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {e.printStackTrace();}
    }


    public void buildTractAndStart() {
        int[] trackList = null;
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        for(int i = 0; i < 16; i++) {
            trackList = new int[16];
            int key = instruments[i];
            for(int j = 0; j < 16; j++){
                JCheckBox jc = (JCheckBox) checkboxList.get(j + (16 * i));
                if (jc.isSelected()) trackList[j] = key;
                else trackList[j] = 0;
            }
            makeTracks(trackList);
            track.add(makeEvent(176, 1, 127, 0, 16));
        }
        track.add(makeEvent(192, 9, 1, 0, 15));
        try{
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {e.printStackTrace();}
    }
    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTractAndStart();
        }
    }
    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        }
    }
    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }
    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        }
    }
    public class MySendListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            boolean[] checkboxState = new boolean[256];
            for(int i = 0; i < 256; i++){
                JCheckBox check = checkboxList.get(i);
                if (check.isSelected()) checkboxState[i] = true;
            }
            try{
                FileOutputStream fileStream = new FileOutputStream(new
                        File("BeatBox"));
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(checkboxState);
            } catch (Exception ex) {ex.printStackTrace();}
        }
    }
    public class MyReadInListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            boolean[] checkboxState = null;
            try{
                FileInputStream fileIn = new FileInputStream(new File("BeatBox"));
                ObjectInputStream is = new ObjectInputStream(fileIn);
                checkboxState = (boolean[]) is.readObject();
            } catch (Exception ex) {ex.printStackTrace();}


            for(int i = 0; i < 256; i++) {
                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if (checkboxState[i]) check.setSelected(true);
                else check.setSelected(false);
            }
            sequencer.stop();
            buildTractAndStart();
        }
    }

    public class MyCheckBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox checkbox = (JCheckBox)e.getSource();
            if (checkbox.isSelected()) {
                checkbox.setIcon(en);
                checkbox.setBackground(purple);
            } else {
                checkbox.setIcon(emptyIcon);
                checkbox.setBackground(backGray);
            }
        }
    }
    public void makeTracks(int[] list) {
        for(int i = 0; i < 16; i++){
            int key = list[i];
            if(key != 0) {
                track.add(makeEvent(144, 9, key, 100, i));
                track.add(makeEvent(128, 9, key, 100, i+1));
            }
        }
    }

    public class MyClearListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
            for (JCheckBox box : checkboxList) {
                box.setSelected(false);
                box.setIcon(emptyIcon);
                box.setBackground(backGray);
            }
        }
    }
    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) {e.printStackTrace();}
        return event;
    }
}